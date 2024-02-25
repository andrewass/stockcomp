package com.stockcomp.participant.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantService(
    private val contestService: ContestService,
    private val participantRepository: ParticipantRepository,
    private val userService: UserService
) : ParticipantService {

    private val logger = LoggerFactory.getLogger(DefaultParticipantService::class.java)

    override fun getActiveParticipantsByUser(username: String): List<Participant> =
        contestService.getContests(listOf(ContestStatus.RUNNING, ContestStatus.STOPPED))
            .flatMap { getAllByEmailAndContest(username, it) }


    override fun getParticipantsSortedByRank(contestNumber: Int, pageNumber: Int, pageSize: Int): Page<Participant> =
        contestService.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByContest(it, PageRequest.of(pageNumber, pageSize, Sort.by("rank"))) }


    override fun getParticipant(contestNumber: Int, email: String): Participant? =
        participantRepository.findByContestAndUser(
            contestService.findByContestNumber(contestNumber),
            userService.findUserByEmail(email)!!
        )

    override fun getLockedParticipant(participantId: Long): Participant =
        participantRepository.findByIdLocked(participantId)

    override fun getAllByContest(contest: Contest): List<Participant> =
        participantRepository.findAllByContest(contest)

    override fun getAllActiveParticipants(): List<Participant> =
        participantRepository.findAllByContestStatus(ContestStatus.RUNNING)

    override fun getAllByEmailAndContest(email: String, contest: Contest): List<Participant> =
        participantRepository.findAllByEmailAndContest(email, contest)


    override fun getParticipantHistory(username: String): List<Participant> =
        userService.findUserByUsername(username)
            .let { participantRepository.findAllByUser(it) }
            .filter { it.contest.contestStatus == ContestStatus.COMPLETED }


    override fun signUpParticipant(email: String, contestNumber: Int) {
        val contest = contestService.findByContestNumber(contestNumber)
        assert(
            contest.contestStatus in listOf(
                ContestStatus.RUNNING, ContestStatus.STOPPED, ContestStatus.AWAITING_START
            )
        )
        Participant(
            user = userService.findUserByEmail(email)!!,
            contest = contest,
            rank = contest.participantCount + 1
        ).also { participantRepository.save(it) }

        contest.participantCount++
        contestService.saveContest(contest)
    }

    override fun saveParticipant(participant: Participant) {
        participantRepository.save(participant)
    }

    override fun maintainParticipantInvestmentValues(contest: Contest) {
        logger.info("Maintaining participant values for contest number : ${contest.contestNumber}")
        getAllByContest(contest)
            .forEach { participant ->
                participant.updateInvestmentValues()
            }
    }

    override fun maintainParticipantRanking(contest: Contest) {
        logger.info("Maintaining participant ranking for contest number : ${contest.contestNumber}")
        var rankCounter = 1
        getAllByContest(contest).sortedByDescending { it.totalValue }
            .forEach { it.rank = rankCounter++ }
    }
}