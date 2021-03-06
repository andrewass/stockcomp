package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.TransactionType
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto
import com.stockcomp.service.util.mapToInvestmentDto
import com.stockcomp.service.util.mapToInvestmentOrder
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

    override fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): InvestmentDto {
        val portfolio = getParticipant(username, contestNumber).portfolio
        val investment = portfolio.investments.firstOrNull { it.symbol == symbol }

        return mapToInvestmentDto(investment, symbol)
    }

    override fun getRemainingFunds(username: String, contestNumber: Int) =
        getParticipant(username, contestNumber).remainingFund

    override fun getTotalInvestmentReturns(username: String, contestNumber: Int): Double =
        getParticipant(username, contestNumber).portfolio.investments
            .map { it.investmentReturns }
            .sum()

    override fun getTotalValueOfInvestments(username: String, contestNumber: Int): Double =
        getParticipant(username, contestNumber).portfolio.investments
            .map { it.totalValue }
            .sum()

    private fun getParticipant(username: String, contestNumber: Int): Participant {
        val contest = contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)

        return participantRepository.findParticipantFromUsernameAndContest(
            username, contest.get()
        ).stream().findFirst().get()
    }
}