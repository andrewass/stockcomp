package com.stockcomp.service.participant

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.Participant
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantService(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository
) : ParticipantService {

    override fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): Investment? =
        getParticipant(username, contestNumber)
            .investments.firstOrNull { it.symbol == symbol }


    override fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<Investment> =
        getParticipant(username, contestNumber).investments


    override fun getTotalValue(username: String, contestNumber: Int): Double =
        getParticipant(username, contestNumber).investments
            .sumOf { it.totalValue }


    private fun getParticipant(username: String, contestNumber: Int): Participant =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findParticipantFromUsernameAndContest(username, it) }
            .first()
}