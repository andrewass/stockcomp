package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.entity.contest.*
import com.stockcomp.exception.InsufficientFundsException
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
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

    fun buyInvestment(request: InvestmentTransactionRequest) {
        val participant = getParticipant(request.username, request.contestNumber)
        val realTimePrice = stockConsumer.findRealTimePrice(request.symbol)

        verifySufficientFunds(participant, realTimePrice, request.amount)
        addInvestmentToPortfolioAndUpdateFunds(participant, realTimePrice, request)
        updateTransactionHistory(participant, request)

        participantRepository.save(participant)
    }

    fun sellInvestment(request: InvestmentTransactionRequest) {
        val participant = getParticipant(request.username, request.contestNumber)
        val realTimePrice = stockConsumer.findRealTimePrice(request.symbol)
    }

    private fun verifySufficientFunds(participant: Participant, realTimePrice: RealTimePriceResponse, amount: Int) {
        if ((realTimePrice.currentPrice!! * amount).toDouble() > participant.remainingFund) {
            throw InsufficientFundsException("Remaining funds ${participant.remainingFund}")
        }
    }

    private fun addInvestmentToPortfolioAndUpdateFunds(
        participant: Participant, realTimePrice: RealTimePriceResponse,
        request: InvestmentTransactionRequest
    ) {
        val portfolio = participant.portfolio
        val investment = portfolio.investments
            .firstOrNull { it.symbol == request.symbol } ?: createNewInvestment(request, portfolio)

        investment.amount += request.amount
        participant.remainingFund -= request.amount * realTimePrice.currentPrice!!
    }

    private fun createNewInvestment(request: InvestmentTransactionRequest, portfolio: Portfolio): Investment {
        return Investment(symbol = request.symbol, portfolio = portfolio, name = request.symbol)
    }

    private fun updateTransactionHistory(participant: Participant,request: InvestmentTransactionRequest) {
        val transaction = Transaction(
            participant = participant,
            symbol = request.symbol,
            dateTimeProcessed = LocalDateTime.now(),
            transactionType = if(request.isBuying) TransactionType.BUY else TransactionType.SELL,
            amount = request.amount
        )
    }


    private fun getParticipant(username: String, contestNumber: Int): Participant {
        val contest = contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)

        return participantRepository.findParticipantFromUsername(username, contest.get()).stream().findFirst().get()
    }
}
