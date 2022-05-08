package com.stockcomp.participant.service

import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.entity.Participant
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.util.toParticipantDto
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


    override fun getSortedParticipantsByRank(contestNumber: Int): List<Participant> =
        contestRepository.findByContestNumber(contestNumber)
            ?.let { participantRepository.findAllByContestOrderByRankAsc(it) }
            ?: throw NoSuchElementException("Unable to get participant list. Contest $contestNumber not found")


    override fun getParticipant(contestNumber: Int, username: String): ParticipantDto =
        contestRepository.findByContestNumber(contestNumber)
            ?.let { contest ->
                userRepository.findByUsername(username)
                    ?.let { user ->
                        participantRepository.findByContestAndUser(contest, user)?.toParticipantDto()
                            ?: throw NoSuchElementException("Participant not found for given user and contest")
                    }
            } ?: throw NoSuchElementException("Contest $contestNumber not found")


    override fun getParticipantHistory(username: String): List<ParticipantDto> =
        userRepository.findByUsername(username)
            ?.let { participantRepository.findAllByUser(it) }
            ?.filter { it.contest.contestStatus in listOf(
                ContestStatus.RUNNING,
                ContestStatus.STOPPED,
                ContestStatus.COMPLETED
            ) }
            ?.map { it.toParticipantDto() }
            ?: throw NoSuchElementException("User not found for username $username")

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