package com.stockcomp.service

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.UpcomingContest
import com.stockcomp.service.order.OrderProcessingService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ContestService(
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository,
    private val participantRepository: ParticipantRepository,
    private val orderProcessingService: OrderProcessingService
) {
    private val logger = LoggerFactory.getLogger(ContestService::class.java)

    fun createContest(request: CreateContestRequest): Contest {
        val contest = Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime
        )
        return contestRepository.save(contest)
    }

    fun startContest(contestNumber: Int) {
        val contest = contestRepository.findContestByContestNumberAndCompletedIsFalseAndRunningIsFalse(contestNumber)
        try {
            contest.get().running = true

            contestRepository.save(contest.get())
            orderProcessingService.startOrderProcessing()
            logger.info("Starting contest")
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Unable to start the contest at the given state")
        }
    }

    fun stopContest(contestNumber: Int) {
        val contest = contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)
        try {
            contest.get().running = false
            contestRepository.save(contest.get())
            orderProcessingService.stopOrderProcessing()
            logger.info("Stopping contest")
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Unable to stop the contest at the given state")
        }
    }

    fun signUpUser(username: String, contestNumber: Int) {
        val contest = contestRepository
            .findContestByContestNumberAndCompletedIsFalseOrRunningIsTrue(contestNumber)
        val user = userRepository.findByUsername(username).get()
        val participant = Participant(user = user, contest = contest)
        contest.participants.add(participant)
        contestRepository.save(contest)
    }

    fun getUpcomingContests(username: String?): List<UpcomingContest> {
        val upcomingContests = contestRepository.findAllByCompletedIsFalse()

        return upcomingContests.map {
            UpcomingContest(
                startTime = it.startTime, contestNumber = it.contestNumber, running = it.completed,
                userParticipating = username?.let { user -> userIsParticipating(user, it.contestNumber) })
        }
    }

    fun userIsParticipating(username: String, contestNumber: Int): Boolean {
        val contest = contestRepository.findContestByContestNumber(contestNumber)
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest.get())

        return participant.isNotEmpty()
    }
}