package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus.RUNNING
import com.stockcomp.domain.contest.enums.OrderStatus.*
import com.stockcomp.domain.contest.enums.TransactionType.BUY
import com.stockcomp.domain.contest.enums.TransactionType.SELL
import com.stockcomp.dto.RealTimePrice
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
class DefaultMaintainOrderService(
    private val participantRepository: ParticipantRepository,
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val investmentRepository: InvestmentRepository,
    private val symbolService: SymbolService,
    private val meterRegistry: SimpleMeterRegistry
) : MaintainOrderService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainOrderService::class.java)

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

    override fun terminateRemainingOrders(contest: Contest) {
        logger.info("Terminating remaining orders")

        investmentOrderRepository.findAllByOrderStatus(ACTIVE)
            .filter { it.participant.contest == contest }
            .map { markInvestmentOrderAsTerminated(it) }
            .let { investmentOrderRepository.saveAll(it) }
    }

    private fun markInvestmentOrderAsTerminated(investmentOrder: InvestmentOrder): InvestmentOrder =
        investmentOrder.apply { this.orderStatus = TERMINATED }


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

    private fun processOrder(investmentOrder: InvestmentOrder, realTimePrice: RealTimePrice) {
        val participant = investmentOrder.participant
        if (investmentOrder.transactionType == BUY && participant.remainingFund >= realTimePrice.usdPrice) {
            processBuyOrder(participant, realTimePrice, investmentOrder)
        } else {
            processSellOrder(participant, realTimePrice, investmentOrder)
        }
    }

    private fun processBuyOrder(participant: Participant, realTimePrice: RealTimePrice, order: InvestmentOrder) {
        if (realTimePrice.price <= order.acceptedPrice) {
            val investment = investmentRepository.findBySymbolAndParticipant(order.symbol, participant)
                ?: Investment(symbol = order.symbol, participant = participant)
            val amountToBuy = getAvailableAmountToBuy(participant, realTimePrice, order)
            investment.averageUnitCost = calculateAverageUnitCost(investment, realTimePrice, amountToBuy)
            investment.amount += amountToBuy
            participant.remainingFund -= amountToBuy * realTimePrice.usdPrice
            postProcessOrder(order, amountToBuy, participant, investment)
        }
    }

    private fun processSellOrder(participant: Participant, realTimePrice: RealTimePrice, order: InvestmentOrder) {
        if (realTimePrice.price >= order.acceptedPrice) {
            val investment = investmentRepository.findBySymbolAndParticipant(order.symbol, participant)
            val amountToSell = min(investment.amount, order.totalAmount)
            investment.amount -= amountToSell
            participant.remainingFund += amountToSell * realTimePrice.usdPrice
            postProcessOrder(order, amountToSell, participant, investment)
        }
    }

    private fun calculateAverageUnitCost(
        investment: Investment, realTimePrice: RealTimePrice, amountToBuy: Int
    ): Double {
        val totalCost = (investment.amount * investment.averageUnitCost) + (amountToBuy * realTimePrice.usdPrice)
        val totalAmount = investment.amount + amountToBuy

        return totalCost / totalAmount
    }

    private fun getAvailableAmountToBuy(
        participant: Participant, realTimePrice: RealTimePrice, order: InvestmentOrder
    ): Int {
        val maxAvailAmount = participant.remainingFund / realTimePrice.usdPrice

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
