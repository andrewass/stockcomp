package com.stockcomp.service.order

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.OrderStatus
import com.stockcomp.domain.contest.OrderStatus.COMPLETED
import com.stockcomp.domain.contest.OrderStatus.FAILED
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.TransactionType.BUY
import com.stockcomp.domain.contest.TransactionType.SELL
import com.stockcomp.repository.InvestmentOrderRepository
import com.stockcomp.repository.InvestmentRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.dto.RealTimePrice
import com.stockcomp.service.symbol.SymbolService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Integer.min

@Service
@Transactional
class DefaultOrderProcessingService(
    private val participantRepository: ParticipantRepository,
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val investmentRepository: InvestmentRepository,
    private val symbolService: SymbolService
) : OrderProcessingService {

    private val logger = LoggerFactory.getLogger(DefaultOrderProcessingService::class.java)
    private var keepProcessingOrders = false

    init {
        startOrderProcessing()
    }

    final override fun startOrderProcessing() {
        keepProcessingOrders = true
        logger.info("Starting order processing")
        CoroutineScope(Default).launch {
            iterateProcessingOrders()
        }
    }

    override fun stopOrderProcessing() {
        keepProcessingOrders = false
        logger.info("Stopping order processing")
    }

    private suspend fun iterateProcessingOrders() {
        while (keepProcessingOrders) {
            try {
                val orders = investmentOrderRepository.findAllByOrderStatus(OrderStatus.ACTIVE)
                val orderMap = orders.groupBy { it.symbol }
                orderMap.forEach { (symbol, orders) ->
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
        logger.info("Order processing is now completed")
    }

    private fun processOrdersForSymbol(symbol: String, orders: List<InvestmentOrder>) {
        val realTimePrice = symbolService.getRealTimePrice(symbol)
        orders.forEach {
            processOrder(it, realTimePrice)
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
