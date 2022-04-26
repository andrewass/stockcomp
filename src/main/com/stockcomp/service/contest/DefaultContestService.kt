package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.domain.contest.enums.ContestStatus.*
import com.stockcomp.dto.contest.ContestParticipantDto
import com.stockcomp.dto.contest.ParticipantDto
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.user.UserService
import com.stockcomp.tasks.ContestTasks
import com.stockcomp.util.mapToContestParticipant
import com.stockcomp.util.toParticipantDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultContestService(
    private val contestRepository: ContestRepository,
    private val userService: UserService,
    private val participantRepository: ParticipantRepository,
    private val contestTasks: ContestTasks
) : ContestService {
    private val logger = LoggerFactory.getLogger(DefaultContestService::class.java)

    override fun startContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndContestStatus(contestNumber, AWAITING_START)?.also {
            it.contestStatus = RUNNING
            contestRepository.save(it)
            contestTasks.startOrderProcessing()
            contestTasks.startMaintainInvestments()
            logger.info("Starting contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to start contest. Contest with number $contestNumber not found")
    }

    override fun stopContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndContestStatus(contestNumber, RUNNING)
            ?.also {
                it.contestStatus = STOPPED
                contestRepository.save(it)
                contestTasks.stopOrderProcessing()
                contestTasks.stopMaintainInvestments()
                logger.info("Stopping contest $contestNumber")
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun completeContest(contestNumber: Int) {
        contestRepository.findByContestNumber(contestNumber)
            ?.takeIf { contest -> contest.contestStatus in listOf(RUNNING, STOPPED) }
            ?.also {
                it.contestStatus = COMPLETED
                contestRepository.save(it)
                contestTasks.stopOrderProcessing()
                contestTasks.stopMaintainInvestments()
                logger.info("Completing contest $contestNumber")
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun signUpUser(username: String, contestNumber: Int): Long {
        return contestRepository.findByContestNumber(contestNumber)
            ?.takeIf { contest -> contest.contestStatus in listOf(RUNNING, STOPPED, AWAITING_START) }
            ?.let {
                val user = userService.findUserByUsername(username)!!
                val participant = Participant(user = user, contest = it, rank = it.participantCount + 1)
                    .let { pcp -> participantRepository.save(pcp) }
                it.participantCount++
                contestRepository.save(it)
                participant.id
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }


    override fun getContest(contestNumber: Int): Contest = contestRepository.findByContestNumber(contestNumber)


    override fun getContests(statusList: List<ContestStatus>): List<Contest> =
        if (statusList.isEmpty()) {
            contestRepository.findAll()
        } else {
            contestRepository.findAllByContestStatusList(statusList)
        }


    override fun getContestParticipants(
        statusList: List<ContestStatus>, username: String
    ): List<ContestParticipantDto> {
        val user = userService.findUserByUsername(username)
        return contestRepository.findAllByContestStatusList(statusList)
            .map {
                val participant: Participant? = participantRepository.findByContestAndUser(it, user)
                mapToContestParticipant(it, participant)
            }
    }

    override fun getSortedParticipantsByRank(contestNumber: Int): List<Participant> =
        contestRepository.findByContestNumber(contestNumber)
            ?.let { participantRepository.findAllByContestOrderByRankAsc(it) }
            ?: throw NoSuchElementException("Unable to get participant list. Contest $contestNumber not found")


    override fun getParticipant(contestNumber: Int, username: String): ParticipantDto =
        contestRepository.findByContestNumber(contestNumber)
            ?.let { contest ->
                userService.findUserByUsername(username)
                    ?.let { user ->
                        participantRepository.findByContestAndUser(contest, user)?.toParticipantDto()
                            ?: throw NoSuchElementException("Participant not found for given user and contest")
                    }
            } ?: throw NoSuchElementException("Contest $contestNumber not found")


    override fun getParticipantHistory(username: String): List<ParticipantDto> =
        userService.findUserByUsername(username)
            ?.let { participantRepository.findAllByUser(it) }
            ?.filter { it.contest.contestStatus in listOf(RUNNING, STOPPED, COMPLETED) }
            ?.map { it.toParticipantDto() }
            ?: throw NoSuchElementException("User not found for username $username")
}