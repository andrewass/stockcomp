package com.stockcomp.participant

import com.stockcomp.contest.domain.Contest
import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.participant.dto.DetailedParticipantDto
import com.stockcomp.participant.dto.mapToDetailedParticipant
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ParticipantService(
    private val contestService: ContestService,
    private val participantRepository: ParticipantRepository,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(ParticipantService::class.java)

    fun getActiveParticipants(email: String): List<Participant> =
        contestService.getActiveContests()
            .mapNotNull { getAllByEmailAndContest(email, it) }

    fun getRunningDetailedParticipantsForSymbol(email: String, symbol: String): List<DetailedParticipantDto> =
        contestService.getRunningContests()
            .mapNotNull { getAllByEmailAndContest(email, it) }
            .map { mapToDetailedParticipant(it, symbol) }

    fun getParticipantsSortedByRank(contestNumber: Int, pageNumber: Int, pageSize: Int): Page<Participant> =
        contestService.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByContest(it, PageRequest.of(pageNumber, pageSize, Sort.by("rank"))) }

    fun getParticipant(contestNumber: Int, email: String): Participant? =
        participantRepository.findByContestAndUser(
            contestService.findByContestNumber(contestNumber),
            userService.findUserByEmail(email)!!
        )

    fun getLockedParticipant(participantId: Long): Participant =
        participantRepository.findByIdLocked(participantId)

    fun getAllByContest(contest: Contest): List<Participant> =
        participantRepository.findAllByContest(contest)

    fun getAllActiveParticipants(): List<Participant> =
        participantRepository.findAllByContestStatus(ContestStatus.RUNNING)


    fun getParticipantHistory(username: String): List<Participant> =
        userService.findUserByUsername(username)
            .let { participantRepository.findAllByUser(it) }
            .filter { it.contest.contestStatus == ContestStatus.COMPLETED }

    fun saveParticipant(participant: Participant) {
        participantRepository.save(participant)
    }

    fun maintainParticipantRanking(contest: Contest) {
        logger.info("Maintaining participant ranking for contest number : ${contest.contestNumber}")
        var rankCounter = 1
        getAllByContest(contest).sortedByDescending { it.totalValue }
            .forEach { it.rank = rankCounter++ }
    }

    private fun getAllByEmailAndContest(email: String, contest: Contest): Participant? =
        participantRepository.findByEmailAndContest(email, contest).firstOrNull()
}