package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.TransactionType
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto
import com.stockcomp.service.util.mapToInvestmentOrder
import com.stockcomp.service.util.toInvestmentDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultInvestmentService(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository
) : InvestmentService {

    override fun placeBuyOrder(request: InvestmentTransactionRequest, username: String) {
        val participant = getParticipant(username, request.contestNumber)
        val order = mapToInvestmentOrder(participant, request, TransactionType.BUY)
        participant.investmentOrders.add(order)
        participantRepository.save(participant)
    }

    override fun placeSellOrder(request: InvestmentTransactionRequest, username: String) {
        val participant = getParticipant(username, request.contestNumber)
        val order = mapToInvestmentOrder(participant, request, TransactionType.SELL)
        participant.investmentOrders.add(order)
        participantRepository.save(participant)
    }

    override fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): InvestmentDto? {
        val portfolio = getParticipant(username, contestNumber).portfolio

        return portfolio.investments.firstOrNull { it.symbol == symbol }?.toInvestmentDto()
    }

    override fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<InvestmentDto> {
        val portfolio = getParticipant(username, contestNumber).portfolio

        return portfolio.investments.map { it.toInvestmentDto() }
    }

    override fun getRemainingFunds(username: String, contestNumber: Int) =
        getParticipant(username, contestNumber).remainingFund

    override fun getTotalValue(username: String, contestNumber: Int): Double =
        getParticipant(username, contestNumber).portfolio.investments
            .map { it.totalValue }
            .sum()

    private fun getParticipant(username: String, contestNumber: Int): Participant {
        val contest = contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)

        return participantRepository.findParticipantFromUsernameAndContest(
            username, contest.get()
        ).stream().findFirst().get()
    }
}