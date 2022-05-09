package com.stockcomp.contest.service

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.dto.ContestParticipantDto
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.service.UserService
import com.stockcomp.contest.tasks.ContestTasks
import com.stockcomp.util.mapToContestParticipant
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
        contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.AWAITING_START)?.also {
            it.contestStatus = ContestStatus.RUNNING
            contestRepository.save(it)
            contestTasks.startOrderProcessing()
            contestTasks.startMaintainInvestments()
            logger.info("Starting contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to start contest. Contest with number $contestNumber not found")
    }

    override fun stopContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
            ?.also {
                it.contestStatus = ContestStatus.STOPPED
                contestRepository.save(it)
                contestTasks.stopOrderProcessing()
                contestTasks.stopMaintainInvestments()
                logger.info("Stopping contest $contestNumber")
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun completeContest(contestNumber: Int) {
        contestRepository.findByContestNumber(contestNumber)
            ?.takeIf { contest -> contest.contestStatus in listOf(ContestStatus.RUNNING, ContestStatus.STOPPED) }
            ?.also {
                it.contestStatus = ContestStatus.COMPLETED
                contestRepository.save(it)
                contestTasks.stopOrderProcessing()
                contestTasks.stopMaintainInvestments()
                logger.info("Completing contest $contestNumber")
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun createContest(request: CreateContestRequest): ContestDto {
        TODO("Not yet implemented")
    }

    override fun getContest(contestNumber: Int): ContestDto =
        mapToContestDto(contestRepository.findByContestNumber(contestNumber))


    override fun deleteContest(contestNumber: Int) {
        TODO("Not yet implemented")
    }

    override fun updateContest(updateContestRequest: UpdateContestRequest): ContestDto {
        TODO("Not yet implemented")
    }

    override fun signUpUser(username: String, contestNumber: Int): Long {
        return contestRepository.findByContestNumber(contestNumber)
            ?.takeIf { contest -> contest.contestStatus in listOf(
                ContestStatus.RUNNING,
                ContestStatus.STOPPED,
                ContestStatus.AWAITING_START
            ) }
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

    override fun getContests(statusList: List<ContestStatus>): List<ContestDto> =
        if (statusList.isEmpty()) {
            contestRepository.findAll()
        } else {
            contestRepository.findAllByContestStatusList(statusList)
        }.map { mapToContestDto(it) }


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
}