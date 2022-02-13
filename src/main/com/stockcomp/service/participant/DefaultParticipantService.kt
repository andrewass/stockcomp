package com.stockcomp.service.participant

import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.dto.contest.InvestmentDto
import com.stockcomp.util.toInvestmentDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantService(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository
) : ParticipantService {

    override fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): InvestmentDto? {
        val participant = getParticipant(username, contestNumber)

        return participant.investments.firstOrNull { it.symbol == symbol }?.toInvestmentDto()
    }

    override fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<InvestmentDto> {
        val participant = getParticipant(username, contestNumber)

        return participant.investments.map { it.toInvestmentDto() }
    }

    override fun getRemainingFunds(username: String, contestNumber: Int) =
        getParticipant(username, contestNumber).remainingFund

    override fun getTotalValue(username: String, contestNumber: Int): Double =
        getParticipant(username, contestNumber).investments
            .map { it.totalValue }
            .sum()

    private fun getParticipant(username: String, contestNumber: Int): Participant {
        val contest = contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)

        return participantRepository.findParticipantFromUsernameAndContest(username, contest)
            .stream().findFirst().get()
    }
}