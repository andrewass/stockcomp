package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.entity.contest.Investment
import com.stockcomp.entity.contest.Participant
import com.stockcomp.entity.contest.Portfolio
import com.stockcomp.entity.contest.Transaction
import com.stockcomp.exception.InsufficientFundsException
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.RealTimePriceResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class InvestmentService @Autowired constructor(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
    private val stockConsumer: StockConsumer
) {

    fun buyInvestment(request: InvestmentTransactionRequest): Transaction {
        val participant = getParticipant(request.username, request.contestNumber)
        val realTimePrice = stockConsumer.findRealTimePrice(request.symbol)

        verifySufficientFunds(participant, realTimePrice, request.amount)
        updatePortfolioAndFundsWhenBuying(participant, realTimePrice, request)
        val transaction = updateTransactionHistory(participant, realTimePrice, request)
        participantRepository.save(participant)

        return transaction
    }

    fun sellInvestment(request: InvestmentTransactionRequest): Transaction {
        val participant = getParticipant(request.username, request.contestNumber)
        val realTimePrice = stockConsumer.findRealTimePrice(request.symbol)

        verifyExistingInvestment(participant, request)
        updatePortfolioAndFundsWhenSelling(participant, realTimePrice, request)
        val transaction = updateTransactionHistory(participant, realTimePrice, request)
        participantRepository.save(participant)

        return transaction
    }

    private fun verifySufficientFunds(participant: Participant, realTimePrice: RealTimePriceResponse, amount: Int) {
        if ((realTimePrice.currentPrice * amount) > participant.remainingFund) {
            throw InsufficientFundsException("Remaining funds ${participant.remainingFund}")
        }
    }

    private fun verifyExistingInvestment(participant: Participant, request: InvestmentTransactionRequest) {
        participant.portfolio.investments
            .filter { it.symbol == request.symbol }
            .find { it.amount >= request.amount }
            ?: throw InsufficientFundsException("Unable to sell requested amount of investments")
    }

    private fun updatePortfolioAndFundsWhenBuying(
        participant: Participant, realTimePrice: RealTimePriceResponse,
        request: InvestmentTransactionRequest
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
        request: InvestmentTransactionRequest
    ): Transaction {
        val transaction = Transaction(
            participant = participant,
            symbol = request.symbol,
            dateTimeProcessed = LocalDateTime.now(),
            transactionType = request.transactionType,
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

        return participantRepository.findParticipantFromUsername(username, contest.get()).stream().findFirst().get()
    }
}
