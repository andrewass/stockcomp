package com.stockcomp.participant.service

import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.participant.entity.Investment
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.dto.mapToParticipantDto
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantService(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
    private val investmentRepository: InvestmentRepository,
    private val userRepository: UserRepository
) : ParticipantService {


    override fun getParticipantsSortedByRank(contestNumber: Int): List<ParticipantDto> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByContestOrderByRankAsc(it) }
            .map { mapToParticipantDto(it) }


    override fun getParticipant(contestNumber: Int, username: String): ParticipantDto =
        mapToParticipantDto(participantRepository.findByContestAndUser(
            contestRepository.findByContestNumber(contestNumber),
            userRepository.findByUsername(username)
        ))


    override fun getParticipantHistory(username: String): List<ParticipantDto> =
        userRepository.findByUsername(username)
            .let { participantRepository.findAllByUser(it) }
            .filter {
                it.contest.contestStatus in listOf(
                    ContestStatus.RUNNING,
                    ContestStatus.STOPPED,
                    ContestStatus.COMPLETED
                )
            }.map { mapToParticipantDto(it) }


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