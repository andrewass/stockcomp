package com.stockcomp.service.order

import com.stockcomp.entity.contest.InvestmentOrder
import com.stockcomp.entity.contest.Investment
import com.stockcomp.entity.contest.Participant
import com.stockcomp.entity.contest.TransactionType
import com.stockcomp.repository.jpa.ParticipantRepository
import org.springframework.stereotype.Service

@Service
class DefaultOrderProcessingService(
    private val participantRepository: ParticipantRepository
) : OrderProcessingService {

    override fun processOrder(investmentOrder: InvestmentOrder, currentPrice: Double) {
        val participant = investmentOrder.participant
        if (investmentOrder.transactionType == TransactionType.BUY) {
            updatePortfolioAndFundsWhenBuying(participant, currentPrice, investmentOrder)
        } else {
            updatePortfolioAndFundsWhenSelling(participant, currentPrice, investmentOrder)
        }
        participantRepository.save(participant)
    }

    private fun updatePortfolioAndFundsWhenBuying(
        participant: Participant, currentPrice: Double, order: InvestmentOrder
    ) {
        val portfolio = participant.portfolio
        var investment = portfolio.investments.firstOrNull { it.symbol == order.symbol }
        if (investment == null) {
            investment = Investment(
                symbol = order.symbol,
                portfolio = participant.portfolio,
                name = order.symbol
            )
            portfolio.investments.add(investment)
        }
        investment.amount += order.totalAmount
        participant.remainingFund -= order.totalAmount * currentPrice
    }

    private fun updatePortfolioAndFundsWhenSelling(
        participant: Participant, currentPrice: Double, order: InvestmentOrder
    ) {
        val investment = participant.portfolio.investments.first { it.symbol == order.symbol }
        investment.amount -= order.totalAmount
        participant.remainingFund += order.totalAmount * currentPrice

        if (investment.amount == 0) {
            participant.portfolio.investments.remove(investment)
        }
    }
}