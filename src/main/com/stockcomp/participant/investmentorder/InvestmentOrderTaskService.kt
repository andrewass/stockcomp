package com.stockcomp.participant.investmentorder

import com.stockcomp.symbol.CurrentPriceSymbolDto
import com.stockcomp.participant.investment.Investment
import com.stockcomp.participant.participant.ParticipantService
import com.stockcomp.participant.participant.Participant
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class InvestmentOrderTaskService(
    private val symbolService: SymbolServiceExternal,
    private val participantService: ParticipantService,
) {

    @Transactional
    fun processInvestmentOrders(participantId: Long) {
        val participant = participantService.getLockedParticipant(participantId)
        participant.investmentOrders
            .filter { it.orderStatus == OrderStatus.ACTIVE }
            .forEach { processOrder(it, participant) }
    }

    private fun processOrder(order: InvestmentOrder, participant: Participant) {
        val currentPrice = symbolService.getCurrentPrice(order.symbol)
        if (order.transactionType == TransactionType.BUY && participant.remainingFunds >= currentPrice.currentPrice) {
            processBuyOrder(participant, currentPrice, order)
        } else if (order.transactionType == TransactionType.SELL) {
            processSellOrder(participant, currentPrice, order)
        }
    }

    private fun processBuyOrder(
        participant: Participant, currentPrice: CurrentPriceSymbolDto, order: InvestmentOrder
    ) {
        if (currentPrice.currentPrice <= order.acceptedPrice) {
            val investment = participant.investments.firstOrNull { it.symbol == order.symbol }
                ?: Investment(symbol = order.symbol, participant = participant)
                    .also { participant.addInvestment(it) }
            val amountToBuy = getAvailableAmountToBuy(participant, currentPrice, order)
            investment.averageUnitCost = calculateAverageUnitCost(investment, currentPrice, amountToBuy)
            investment.amount += amountToBuy
            participant.remainingFunds -= amountToBuy * currentPrice.currentPrice
            postProcessOrder(order, amountToBuy, participant, investment)
        }
    }

    private fun processSellOrder(
        participant: Participant, currentPrice: CurrentPriceSymbolDto, order: InvestmentOrder
    ) {
        if (currentPrice.currentPrice >= order.acceptedPrice) {
            val investment = participant.investments.first { it.symbol == order.symbol }
            val amountToSell = Integer.min(investment.amount, order.totalAmount)
            investment.amount -= amountToSell
            participant.remainingFunds += amountToSell * currentPrice.currentPrice
            postProcessOrder(order, amountToSell, participant, investment)
        }
    }

    private fun calculateAverageUnitCost(
        investment: Investment, currentPrice: CurrentPriceSymbolDto, amountToBuy: Int
    ): Double {
        val totalCost = (investment.amount * investment.averageUnitCost) + (amountToBuy * currentPrice.currentPrice)
        val totalAmount = investment.amount + amountToBuy
        return totalCost / totalAmount
    }

    private fun getAvailableAmountToBuy(
        participant: Participant, currentPrice: CurrentPriceSymbolDto, order: InvestmentOrder
    ): Int {
        val maxAvailAmount = participant.remainingFunds / currentPrice.currentPrice
        return Integer.min(maxAvailAmount.toInt(), order.remainingAmount)
    }

    private fun postProcessOrder(
        order: InvestmentOrder, amountProcessed: Int, participant: Participant, investment: Investment
    ) {
        order.remainingAmount -= amountProcessed
        if (order.remainingAmount == 0) {
            order.orderStatus = OrderStatus.COMPLETED
        }
        if (investment.amount == 0) {
            participant.removeInvestment(investment)
        }
        participantService.saveParticipant(participant)
    }
}