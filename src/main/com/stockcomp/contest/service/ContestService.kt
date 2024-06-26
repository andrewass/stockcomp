package com.stockcomp.contest.service

import com.stockcomp.contest.domain.Contest
import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.domain.ContestStatus.*
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ContestService(
    private val contestRepository: ContestRepository,
    private val userService: UserService,
) {

    fun createContest(contestNumber: Int, startTime: LocalDateTime) {
        Contest(
            contestNumber = contestNumber,
            startTime = startTime,
            endTime = startTime.plusMonths(2)
        ).also { contestRepository.save(it) }
    }

    fun signUpToContest(email: String, contestNumber: Int) {
        val contest = contestRepository.findByContestNumber(contestNumber)
        assert(contest.contestStatus in listOf(RUNNING, STOPPED, AWAITING_START))
        Participant(
            user = userService.findUserByEmail(email)!!,
            contest = contest,
            rank = contest.getParticipantCount() + 1
        ).also { contest.addParticipant(it) }
        contestRepository.save(contest)
    }

    fun deleteContest(contestNumber: Int) {
        contestRepository.deleteByContestNumber(contestNumber)
    }

    fun updateContest(number: Int, status: ContestStatus, start: LocalDateTime) {
        contestRepository.findByContestNumber(number).apply {
            contestStatus = status
            startTime = start
            endTime = startTime.plusMonths(2)
        }.also { contestRepository.save(it) }
    }

    fun getActiveContestsSignedUp(email: String): List<Contest> =
        userService.findUserByEmail(email)
            .let { return contestRepository.getAllActiveContestsSignedUp(it!!) }

    fun getActiveContestsNotSignedUp(email: String): List<Contest> =
        userService.findUserByEmail(email)
            .let { contestRepository.getAllActiveContestsNotSignedUp(it!!.id!!) }

    fun getActiveContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(
            listOf(RUNNING, STOPPED, AWAITING_START)
        )

    fun getRunningContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(listOf(RUNNING))

    fun getCompletedContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(listOf(COMPLETED))

    fun getAllContestsSorted(pageNumber: Int, pageSize: Int): Page<Contest> =
        contestRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("contestNumber")))

    fun findByContestNumber(contestNumber: Int): Contest =
        contestRepository.findByContestNumber(contestNumber)

    fun saveContest(contest: Contest) {
        contestRepository.save(contest)
    }
}