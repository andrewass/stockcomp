package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.entity.contest.*
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.RealTimePriceResponse
import java.time.LocalDateTime

//TODO: Delete class when relevant code is moved to new service
class OldInvestmentService(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
    private val stockConsumer: StockConsumer
) {

    private fun updatePortfolioAndFundsWhenBuying(
        participant: Participant, realTimePrice: RealTimePriceResponse, request: InvestmentTransactionRequest
    ) {
        val portfolio = participant.portfolio
        var investment = portfolio.investments.firstOrNull { it.symbol == request.symbol }
        if (investment == null) {
            investment = createNewInvestment(request, portfolio)
            portfolio.investments.add(investment)
        }
        investment.amount += request.amount
        participant.remainingFund -= request.amount * realTimePrice.currentPrice
    }

    private fun updatePortfolioAndFundsWhenSelling(
        participant: Participant, realTimePrice: RealTimePriceResponse,
        request: InvestmentTransactionRequest
    ) {
        val investment = participant.portfolio.investments.first { it.symbol == request.symbol }
        investment.amount -= request.amount
        participant.remainingFund += request.amount * realTimePrice.currentPrice

        if (investment.amount == 0) {
            participant.portfolio.investments.remove(investment)
        }
    }

    private fun updateTransactionHistory(
        participant: Participant, realTimePrice: RealTimePriceResponse,
        request: InvestmentTransactionRequest, transactionType: TransactionType
    ): Transaction {
        val transaction = Transaction(
            participant = participant,
            symbol = request.symbol,
            dateTimeProcessed = LocalDateTime.now(),
            transactionType = transactionType,
            amount = request.amount,
            currentPrice = realTimePrice.currentPrice
        )
        participant.transactions.add(transaction)

        return transaction
    }

    private fun createNewInvestment(request: InvestmentTransactionRequest, portfolio: Portfolio): Investment {
        return Investment(symbol = request.symbol, portfolio = portfolio, name = request.symbol)
    }

    private fun getParticipant(username: String, contestNumber: Int): Participant {
        val contest = contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)

        return participantRepository.findParticipantFromUsernameAndContest(username, contest.get()).stream().findFirst()
            .get()
    }
}
