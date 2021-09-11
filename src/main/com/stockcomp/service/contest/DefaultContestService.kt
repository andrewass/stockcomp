package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Participant
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.response.UpcomingContest
import com.stockcomp.service.order.OrderProcessingService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultContestService(
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository,
    private val participantRepository: ParticipantRepository,
    private val orderProcessingService: OrderProcessingService
) : ContestService {
    private val logger = LoggerFactory.getLogger(DefaultContestService::class.java)

    override fun startContest(contestNumber: Int) {
        contestRepository.findContestByContestNumberAndCompletedIsFalseAndRunningIsFalse(contestNumber)?.let {
            it.startContest()
            contestRepository.save(it)
            orderProcessingService.startOrderProcessing()
            logger.info("Starting contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to start contest. Contest with number $contestNumber not found")
    }

    override fun stopContest(contestNumber: Int) {
        contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)?.let {
            it.stopContest()
            contestRepository.save(it)
            orderProcessingService.stopOrderProcessing()
            logger.info("Stopping contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to stop contest. Contest with number $contestNumber not found")
    }

    override fun completeContest(contestNumber: Int) {
        contestRepository.findContestByContestNumberAndCompletedIsFalse(contestNumber)?.let{
            it.completeContest()
            contestRepository.save(it)
            orderProcessingService.stopOrderProcessing()
            logger.info("Completing contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to complete contest. Contest with number $contestNumber not found")
    }


    override fun signUpUser(username: String, contestNumber: Int) {
        val contest = contestRepository.findContestByContestNumber(contestNumber)
        val user = userRepository.findByUsername(username)
        val participant = Participant(user = user, contest = contest)
        contest.participants.add(participant)
        contestRepository.save(contest)
    }

    override fun getUpcomingContests(username: String): List<UpcomingContest> {
        val upcomingContests = contestRepository.findAllByCompletedIsFalse()

        return upcomingContests.map {
            UpcomingContest(
                startTime = it.startTime, contestNumber = it.contestNumber, running = it.running,
                userParticipating = userIsParticipating(username, it.contestNumber)
            )
        }
    }

    override fun userIsParticipating(username: String, contestNumber: Int): Boolean {
        val contest = contestRepository.findContestByContestNumber(contestNumber)
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest)

        return participant.isNotEmpty()
    }
}