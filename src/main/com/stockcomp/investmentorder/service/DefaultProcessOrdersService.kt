package com.stockcomp.investmentorder.service

import com.stockcomp.participant.entity.Investment
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.participant.entity.Participant
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.investmentorder.entity.OrderStatus
import com.stockcomp.investmentorder.entity.TransactionType
import com.stockcomp.contest.dto.RealTimePrice
import com.stockcomp.investmentorder.repository.InvestmentOrderRepository
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.contest.service.SymbolService
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultProcessOrdersService(
    private val participantRepository: ParticipantRepository,
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val investmentRepository: InvestmentRepository,
    private val symbolService: SymbolService,
    private val meterRegistry: SimpleMeterRegistry
) : ProcessOrdersService {

    private val logger = LoggerFactory.getLogger(DefaultProcessOrdersService::class.java)

    override suspend fun processInvestmentOrders() {
        try {
            investmentOrderRepository.findAllByOrderAndContestStatus(OrderStatus.ACTIVE, ContestStatus.RUNNING)
                .also { gaugeOrders(it) }
                .groupBy { it.symbol }
                .forEach { (symbol, orders) ->
                    run {
                        delay(15000L)
                        processOrdersForSymbol(symbol, orders)
                        logger.info("Processing order for symbol $symbol")
                    }
                }
        } catch (e: Exception) {
            logger.error("Failed order processing : ${e.message}")
        }
    }

    private fun gaugeOrders(orders: List<InvestmentOrder>) {
        Gauge.builder("active.orders", orders) { list -> list.size.toDouble() }
            .description("Number of active orders")
            .register(meterRegistry)
    }

    private fun processOrdersForSymbol(symbol: String, orders: List<InvestmentOrder>) {
        symbolService.getRealTimePrice(symbol)
            .also { realTimePrice ->
                orders.forEach { processOrder(it, realTimePrice) }
            }
    }

    private fun processOrder(investmentOrder: InvestmentOrder, realTimePriceDto: RealTimePrice) {
        val participant = investmentOrder.participant
        if (investmentOrder.transactionType == TransactionType.BUY && participant.remainingFunds >= realTimePriceDto.usdPrice) {
            processBuyOrder(participant, realTimePriceDto, investmentOrder)
        } else {
            processSellOrder(participant, realTimePriceDto, investmentOrder)
        }
    }

    private fun processBuyOrder(participant: Participant, realTimePriceDto: RealTimePrice, order: InvestmentOrder) {
        if (realTimePriceDto.price <= order.acceptedPrice) {
            val investment = investmentRepository.findBySymbolAndParticipant(order.symbol, participant)
                ?: Investment(symbol = order.symbol, participant = participant)
            val amountToBuy = getAvailableAmountToBuy(participant, realTimePriceDto, order)
            investment.averageUnitCost = calculateAverageUnitCost(investment, realTimePriceDto, amountToBuy)
            investment.amount += amountToBuy
            participant.remainingFunds -= amountToBuy * realTimePriceDto.usdPrice
            postProcessOrder(order, amountToBuy, participant, investment)
        }
    }

    private fun processSellOrder(participant: Participant, realTimePriceDto: RealTimePrice, order: InvestmentOrder) {
        if (realTimePriceDto.price >= order.acceptedPrice) {
            val investment = investmentRepository.findBySymbolAndParticipant(order.symbol, participant)
            val amountToSell = Integer.min(investment.amount, order.totalAmount)
            investment.amount -= amountToSell
            participant.remainingFunds += amountToSell * realTimePriceDto.usdPrice
            postProcessOrder(order, amountToSell, participant, investment)
        }
    }

    private fun calculateAverageUnitCost(
        investment: Investment, realTimePriceDto: RealTimePrice, amountToBuy: Int
    ): Double {
        val totalCost = (investment.amount * investment.averageUnitCost) + (amountToBuy * realTimePriceDto.usdPrice)
        val totalAmount = investment.amount + amountToBuy

        return totalCost / totalAmount
    }

    private fun getAvailableAmountToBuy(
        participant: Participant, realTimePriceDto: RealTimePrice, order: InvestmentOrder
    ): Int {
        val maxAvailAmount = participant.remainingFunds / realTimePriceDto.usdPrice

        return Integer.min(maxAvailAmount.toInt(), order.remainingAmount)
    }

    private fun postProcessOrder(
        order: InvestmentOrder, amountProcessed: Int, participant: Participant, investment: Investment
    ) {
        order.remainingAmount -= amountProcessed
        if (order.remainingAmount == 0) {
            order.orderStatus = OrderStatus.COMPLETED
        } else if (order.transactionType == TransactionType.SELL && order.remainingAmount > 0) {
            order.apply {
                orderStatus = OrderStatus.FAILED
                errorMessage = "Failed to complete sell order. Remaining ${order.remainingAmount} " +
                        "shares not found in portfolio"
            }
        }
        if (investment.amount > 0) investmentRepository.save(investment) else investmentRepository.delete(investment)
        investmentOrderRepository.save(order)
        participantRepository.save(participant)
    }
}