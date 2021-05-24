package com.stockcomp.service.order

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.OrderStatus
import com.stockcomp.domain.contest.OrderStatus.COMPLETED
import com.stockcomp.domain.contest.OrderStatus.FAILED
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.TransactionType.BUY
import com.stockcomp.domain.contest.TransactionType.SELL
import com.stockcomp.repository.jpa.InvestmentOrderRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.service.StockService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Integer.min

@Service
class DefaultOrderProcessingService(
    private val participantRepository: ParticipantRepository,
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val stockService: StockService
) : OrderProcessingService {

    private val logger = LoggerFactory.getLogger(DefaultOrderProcessingService::class.java)
    private var launchedJob: Job? = null

    override fun startOrderProcessing() {
        if (launchedJob?.isActive == true) {
            logger.warn("Unable to start order processing. Previous order processing still running")
            return
        }
        logger.info("Launching order processing")
        launchedJob = GlobalScope.launch {
            iterateProcessingOrders()
        }
    }

    override fun stopOrderProcessing() {
        launchedJob?.cancel()
        logger.info("Stopping order processing")
    }

    private suspend fun iterateProcessingOrders() {
        while (true) {
            val orders = investmentOrderRepository.findAllByOrderStatus(OrderStatus.ACTIVE)
            val orderMap = orders.groupBy { it.symbol }
            orderMap.forEach { (symbol, orders) ->
                run {
                    processOrdersForSymbol(symbol, orders)
                    delay(1500L)
                }
            }
        }
    }

    private fun processOrdersForSymbol(symbol: String, orders: List<InvestmentOrder>) {
        val symbolPrice = stockService.getRealTimePrice(symbol)
        orders.forEach {
            processOrder(it, symbolPrice.currentPrice)
        }
    }

    private fun processOrder(investmentOrder: InvestmentOrder, currentPrice: Double) {
        val participant = investmentOrder.participant
        if (investmentOrder.transactionType == BUY && participant.remainingFund >= currentPrice) {
            processBuyOrder(participant, currentPrice, investmentOrder)
        } else {
            processSellOrder(participant, currentPrice, investmentOrder)
        }
        participantRepository.save(participant)
    }

    private fun processBuyOrder(
        participant: Participant, currentPrice: Double, order: InvestmentOrder
    ) {
        val portfolio = participant.portfolio
        val investment = portfolio.investments.firstOrNull { it.symbol == order.symbol } ?: Investment(
            symbol = order.symbol,
            portfolio = participant.portfolio,
            name = order.symbol
        ).also { portfolio.investments.add(it) }

        val amountBought = getAvailableAmountToBuy(participant, currentPrice, order)
        investment.amount += amountBought
        participant.remainingFund -= amountBought * currentPrice
        postProcessOrder(order, amountBought)
    }

    private fun processSellOrder(
        participant: Participant, currentPrice: Double, order: InvestmentOrder
    ) {
        val investment = participant.portfolio.investments.first { it.symbol == order.symbol }
        val amountSold = min(investment.amount, order.totalAmount)
        investment.amount -= amountSold
        participant.remainingFund += amountSold * currentPrice
        if (investment.amount == 0) {
            participant.portfolio.investments.remove(investment)
        }
        postProcessOrder(order, amountSold)
    }

    private fun getAvailableAmountToBuy(participant: Participant, currentPrice: Double, order: InvestmentOrder): Int {
        val maxAvailAmount = participant.remainingFund % currentPrice

        return min(maxAvailAmount.toInt(), order.remainingAmount)
    }

    private fun postProcessOrder(order: InvestmentOrder, amountProcessed: Int) {
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
    }
}
