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
import com.stockcomp.response.RealTimePrice
import com.stockcomp.service.symbol.SymbolService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Integer.min

@Service
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
        GlobalScope.launch {
            iterateProcessingOrders()
        }
    }

    override fun stopOrderProcessing() {
        keepProcessingOrders = false
        logger.info("Stopping order processing")
    }

    private suspend fun iterateProcessingOrders() {
        logger.info("Launching order processing")
        while (true) {
            val orders = investmentOrderRepository.findAllByOrderStatus(OrderStatus.ACTIVE)
            val orderMap = orders.groupBy { it.symbol }
            orderMap.forEach { (symbol, orders) ->
                run {
                    delay(15000L)
                    processOrdersForSymbol(symbol, orders)
                    logger.info("Processing order for symbol $symbol")
                }
            }
        }
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
            val investment = investmentRepository.findBySymbolAndPortfolio(order.symbol, participant.portfolio)
                ?: investmentRepository.save(
                    Investment(
                        symbol = order.symbol,
                        portfolio = participant.portfolio,
                        name = order.symbol
                    )
                )
            val amountToBuy = getAvailableAmountToBuy(participant, realTimePrice, order)
            investment.averageUnitCost = calculateAverageUnitCost(investment, realTimePrice, amountToBuy)
            investment.amount += amountToBuy
            participant.remainingFund -= amountToBuy * realTimePrice.usdPrice
            postProcessOrder(order, amountToBuy, investment, participant)
        }
    }

    private fun calculateAverageUnitCost(
        investment: Investment, realTimePrice: RealTimePrice, amountToBuy: Int
    ): Double {
        val totalCost = (investment.amount * investment.averageUnitCost) + (amountToBuy * realTimePrice.usdPrice)
        val totalAmount = investment.amount + amountToBuy

        return totalCost / totalAmount
    }

    private fun processSellOrder(participant: Participant, realTimePrice: RealTimePrice, order: InvestmentOrder) {
        if (realTimePrice.price >= order.acceptedPrice) {
            val investment = investmentRepository.findBySymbolAndPortfolio(order.symbol, participant.portfolio)
            val amountToSell = min(investment.amount, order.totalAmount)
            investment.amount -= amountToSell
            participant.remainingFund += amountToSell * realTimePrice.usdPrice
            if (investment.amount == 0) {
                investmentRepository.delete(investment)
            }
            postProcessOrder(order, amountToSell, investment, participant)
        }
    }

    private fun getAvailableAmountToBuy(
        participant: Participant, realTimePrice: RealTimePrice, order: InvestmentOrder
    ): Int {
        val maxAvailAmount = participant.remainingFund % realTimePrice.usdPrice

        return min(maxAvailAmount.toInt(), order.remainingAmount)
    }

    private fun postProcessOrder(
        order: InvestmentOrder, amountProcessed: Int, investment: Investment, participant: Participant
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
        investmentOrderRepository.save(order)
        investmentRepository.save(investment)
        participantRepository.save(participant)
    }
}
