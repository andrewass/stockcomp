package com.stockcomp.service.order

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus.RUNNING
import com.stockcomp.domain.contest.enums.OrderStatus.*
import com.stockcomp.domain.contest.enums.TransactionType.BUY
import com.stockcomp.domain.contest.enums.TransactionType.SELL
import com.stockcomp.dto.stock.RealTimePriceDto
import com.stockcomp.repository.InvestmentOrderRepository
import com.stockcomp.repository.InvestmentRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.symbol.SymbolService
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Integer.min

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
            investmentOrderRepository.findAllByOrderAndContestStatus(ACTIVE, RUNNING)
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

    private fun processOrder(investmentOrder: InvestmentOrder, realTimePriceDto: RealTimePriceDto) {
        val participant = investmentOrder.participant
        if (investmentOrder.transactionType == BUY && participant.remainingFunds >= realTimePriceDto.usdPrice) {
            processBuyOrder(participant, realTimePriceDto, investmentOrder)
        } else {
            processSellOrder(participant, realTimePriceDto, investmentOrder)
        }
    }

    private fun processBuyOrder(participant: Participant, realTimePriceDto: RealTimePriceDto, order: InvestmentOrder) {
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

    private fun processSellOrder(participant: Participant, realTimePriceDto: RealTimePriceDto, order: InvestmentOrder) {
        if (realTimePriceDto.price >= order.acceptedPrice) {
            val investment = investmentRepository.findBySymbolAndParticipant(order.symbol, participant)
            val amountToSell = min(investment.amount, order.totalAmount)
            investment.amount -= amountToSell
            participant.remainingFunds += amountToSell * realTimePriceDto.usdPrice
            postProcessOrder(order, amountToSell, participant, investment)
        }
    }

    private fun calculateAverageUnitCost(
        investment: Investment, realTimePriceDto: RealTimePriceDto, amountToBuy: Int
    ): Double {
        val totalCost = (investment.amount * investment.averageUnitCost) + (amountToBuy * realTimePriceDto.usdPrice)
        val totalAmount = investment.amount + amountToBuy

        return totalCost / totalAmount
    }

    private fun getAvailableAmountToBuy(
        participant: Participant, realTimePriceDto: RealTimePriceDto, order: InvestmentOrder
    ): Int {
        val maxAvailAmount = participant.remainingFunds / realTimePriceDto.usdPrice

        return min(maxAvailAmount.toInt(), order.remainingAmount)
    }

    private fun postProcessOrder(
        order: InvestmentOrder, amountProcessed: Int, participant: Participant, investment: Investment
    ) {
        order.remainingAmount -= amountProcessed
        if (order.remainingAmount == 0) {
            order.orderStatus = COMPLETED
        } else if (order.transactionType == SELL && order.remainingAmount > 0) {
            order.apply {
                orderStatus = FAILED
                errorMessage = "Failed to complete sell order. Remaining ${order.remainingAmount} " +
                        "shares not found in portfolio"
            }
        }
        if (investment.amount > 0) investmentRepository.save(investment) else investmentRepository.delete(investment)
        investmentOrderRepository.save(order)
        participantRepository.save(participant)
    }
}
