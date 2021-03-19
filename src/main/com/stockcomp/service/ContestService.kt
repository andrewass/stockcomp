package com.stockcomp.service

import com.stockcomp.entity.contest.Contest
import com.stockcomp.entity.contest.Participant
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ContestService @Autowired constructor(
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository
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
        val contest = contestRepository.findContestByContestNumberAndInPreStartModeIsTrue(contestNumber)

        try {
            contest.get().apply {
                inPreStartMode = false
                inRunningMode = true
            }
            contestRepository.save(contest.get())
            logger.info("Starting contest")
            startKafkaConsumersForContest()
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Unable to start the contest at the given state")
        }
    }

    fun stopContest(contestNumber: Int) {
        val contest = contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)
        try {
            contest.get().inRunningMode = false
            contestRepository.save(contest.get())
            logger.info("Stopping contest")
            stopKafkaConsumersForContest()
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Unable to stop the contest at the given state")
        }
    }

    fun signUpUser(username: String, contestNumber: Int) {
        val contest = contestRepository.findContestByContestNumberAndInPreStartModeIsTrue(contestNumber).get()
        val user = userRepository.findByUsername(username).get()
        val participant = Participant(user = user, contest = contest)

        contest.participants.add(participant)
        contestRepository.save(contest)
    }

    fun getUpcomingContests() : List<Contest> {
        return emptyList()
    }

    private fun startKafkaConsumersForContest() {
        //endpointRegistry.allListenerContainers.forEach { it.start() }
    }

    private fun stopKafkaConsumersForContest() {
        //endpointRegistry.allListenerContainers.forEach { it.stop() }
    }
}