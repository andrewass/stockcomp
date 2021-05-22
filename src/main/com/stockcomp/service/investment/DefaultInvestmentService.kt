package com.stockcomp.service.investment

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.entity.contest.Participant
import com.stockcomp.entity.contest.TransactionType
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto
import com.stockcomp.service.util.mapToAwaitingOrder
import com.stockcomp.service.util.mapToInvestmentDto
import org.springframework.stereotype.Service

@Service
class DefaultInvestmentService(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
    private val stockConsumer: StockConsumer
) : InvestmentService {

    override fun placeBuyOrder(request: InvestmentTransactionRequest, username: String) {
        val participant = getParticipant(username, request.contestNumber)
        val order = mapToAwaitingOrder(participant, request, TransactionType.BUY)
        participant.awaitingOrders.add(order)
        participantRepository.save(participant)
    }

    override fun placeSellOrder(request: InvestmentTransactionRequest, username: String) {
        val participant = getParticipant(username, request.contestNumber)
        val order = mapToAwaitingOrder(participant, request, TransactionType.BUY)
        participant.awaitingOrders.add(order)
        participantRepository.save(participant)
    }

    override fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): InvestmentDto {
        val portfolio = getParticipant(username, contestNumber).portfolio
        val investment = portfolio.investments.firstOrNull { it.symbol == symbol }

        return mapToInvestmentDto(investment, symbol)
    }

    override fun getRemainingFunds(username: String, contestNumber: Int): Double {
        TODO("Not yet implemented")
    }

    private fun getParticipant(username: String, contestNumber: Int): Participant {
        val contest = contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)

        return participantRepository.findParticipantFromUsernameAndContest(
            username, contest.get()
        ).stream().findFirst().get()
    }
}