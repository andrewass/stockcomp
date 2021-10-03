package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.response.ParticipantDto
import com.stockcomp.response.UpcomingContestParticipantDto
import com.stockcomp.service.order.OrderProcessingService
import com.stockcomp.service.user.UserService
import com.stockcomp.service.util.mapToUpcomingContestParticipantDto
import com.stockcomp.service.util.toParticipantDto
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
        contestRepository.findByContestNumberAndCompletedIsFalseAndRunningIsFalse(contestNumber)?.let {
            it.startContest()
            contestRepository.save(it)
            orderProcessingService.startOrderProcessing()
            logger.info("Starting contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to start contest. Contest with number $contestNumber not found")
    }

    override fun stopContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndRunningIsTrue(contestNumber)?.let {
            it.stopContest()
            contestRepository.save(it)
            orderProcessingService.stopOrderProcessing()
            logger.info("Stopping contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to stop contest. Contest with number $contestNumber not found")
    }

    override fun completeContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndCompleted(contestNumber, false)?.let {
            it.completeContest()
            contestRepository.save(it)
            orderProcessingService.stopOrderProcessing()
            logger.info("Completing contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to complete contest. Contest with number $contestNumber not found")
    }

    override fun signUpUser(username: String, contestNumber: Int) {
        contestRepository.findByContestNumberAndCompleted(contestNumber, false)?.let {
            val user = userService.findUserByUsername(username)!!
            val participant = Participant(user = user, contest = it)
            participantRepository.save(participant)
            it.participantCount.inc()
            contestRepository.save(it)
        } ?: throw NoSuchElementException("Unable to sign up user. Contest $contestNumber not found")
    }

    override fun getUpcomingContestsParticipant(username: String): List<UpcomingContestParticipantDto> {
        val upcomingContests = contestRepository.findAllByCompleted(false)

        return upcomingContests.map { createUpcomingContestParticipantDto(username, it) }
    }

    override fun getParticipantsByTotalValue(contestNumber: Int): List<ParticipantDto> {
        contestRepository.findByContestNumber(contestNumber)?.let { contest ->
            val participants = participantRepository.findAllByContestOrderByTotalValueDesc(contest)

            return participants.map { it.toParticipantDto() }
        } ?: throw NoSuchElementException("Unable to get participant list. Contest $contestNumber not found")
    }

    override fun getParticipant(contestNumber: Int, username: String): ParticipantDto {
        contestRepository.findByContestNumber(contestNumber)?.let { contest ->
            userService.findUserByUsername(username)?.let { user ->
                return participantRepository.findByContestAndUser(contest, user)?.toParticipantDto()
                    ?: throw NoSuchElementException("Participant not found for given user and contest")
            }
        } ?: throw NoSuchElementException("Contest $contestNumber not found")
    }

    private fun createUpcomingContestParticipantDto(username: String, contest: Contest): UpcomingContestParticipantDto {
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest)

        return mapToUpcomingContestParticipantDto(contest, participant)
    }
}