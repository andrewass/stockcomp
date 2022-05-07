package com.stockcomp.participant.service

import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.entity.Participant
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.repository.ParticipantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantService(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
    private val investmentRepository: InvestmentRepository
) : ParticipantService {

    override fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): Investment? =
        getParticipant(username, contestNumber)
            .investments.firstOrNull { it.symbol == symbol }


    override fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<Investment> =
        getParticipant(username, contestNumber)
            .let { investmentRepository.findAllByParticipant(it) }


    private fun getParticipant(username: String, contestNumber: Int): Participant =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByUsernameAndContest(username, it) }
            .first()
}