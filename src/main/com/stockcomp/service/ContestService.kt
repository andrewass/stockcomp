package com.stockcomp.service

import com.stockcomp.entity.contest.Contest
import com.stockcomp.entity.contest.Participant
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.repository.jpa.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.UpcomingContest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ContestService(
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository,
    private val participantRepository: ParticipantRepository
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

    fun getUpcomingContests(): List<UpcomingContest> {
        val upcomingContests = contestRepository.findAllByInRunningModeIsTrueOrInPreStartModeIsTrue()

        return upcomingContests.map {
            UpcomingContest(
                startTime = it.startTime, contestNumber = it.contestNumber,
                inPreStartMode = it.inPreStartMode, inRunningMode = it.inRunningMode
            )
        }
    }

    fun userIsParticipating(username: String, contestNumber: Int): Boolean {
        val contest = contestRepository.findContestByContestNumber(contestNumber)
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest.get())

        return participant.isNotEmpty()
    }
}