package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus.*
import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.ParticipantDto
import com.stockcomp.dto.UpcomingContestParticipantDto
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.order.OrderProcessingService
import com.stockcomp.service.user.UserService
import com.stockcomp.util.mapToUpcomingContestParticipantDto
import com.stockcomp.util.toContestDto
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
    private val orderProcessingService: OrderProcessingService
) : ContestService {
    private val logger = LoggerFactory.getLogger(DefaultContestService::class.java)

    override fun startContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndContestStatus(contestNumber, AWAITING_START)?.also {
            it.contestStatus = RUNNING
            contestRepository.save(it)
            orderProcessingService.startOrderProcessing()
            logger.info("Starting contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to start contest. Contest with number $contestNumber not found")
    }

    override fun stopContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndContestStatus(contestNumber, RUNNING)
            ?.also {
                it.contestStatus = STOPPED
                contestRepository.save(it)
                orderProcessingService.stopOrderProcessing()
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
                orderProcessingService.stopOrderProcessing()
                logger.info("Completing contest $contestNumber")
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun signUpUser(username: String, contestNumber: Int) {
        contestRepository.findByContestNumber(contestNumber)
            ?.takeIf { contest -> contest.contestStatus in listOf(RUNNING, STOPPED, AWAITING_START) }
            ?.also {
                val user = userService.findUserByUsername(username)!!
                val participant = Participant(user = user, contest = it, rank = it.participantCount+1)
                participantRepository.save(participant)
                it.participantCount++
                contestRepository.save(it)
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun getAllContests(): List<ContestDto> =
        contestRepository.findAll()
            .sortedByDescending { it.startTime }
            .map { it.toContestDto() }


    override fun getUpcomingContestsParticipant(username: String): List<UpcomingContestParticipantDto> =
        contestRepository.findAll()
            .filter { listOf(RUNNING, AWAITING_START).contains(it.contestStatus) }
            .map { createUpcomingContestParticipantDto(username, it) }


    override fun getParticipantsByTotalValue(contestNumber: Int): List<ParticipantDto> =
        contestRepository.findByContestNumber(contestNumber)
            ?.let { participantRepository.findAllByContestOrderByTotalValueDesc(it) }
            ?.let { it.map { participant -> participant.toParticipantDto() } }
            ?: throw NoSuchElementException("Unable to get participant list. Contest $contestNumber not found")


    override fun getParticipantsByRank(contestNumber: Int): List<ParticipantDto> =
        contestRepository.findByContestNumber(contestNumber)
            ?.let { participantRepository.findAllByContestOrderByRankAsc(it) }
            ?.let { it.map { participant -> participant.toParticipantDto() } }
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


    private fun createUpcomingContestParticipantDto(username: String, contest: Contest): UpcomingContestParticipantDto =
        mapToUpcomingContestParticipantDto(
            contest,
            participantRepository.findParticipantFromUsernameAndContest(username, contest)
        )
}