package com.stockcomp.contest.service

import com.stockcomp.contest.domain.Contest
import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.domain.ContestStatus.*
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.user.internal.UserServiceInternal
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ContestServiceInternal(
    private val contestRepository: ContestRepository,
    private val userService: UserServiceInternal,
) {

    fun createContest(contestNumber: Int, startTime: LocalDateTime) {
        Contest(
            contestNumber = contestNumber,
            startTime = startTime,
            endTime = startTime.plusMonths(2)
        ).also { contestRepository.save(it) }
    }

    fun getContest(contestId: Long) : Contest =
        contestRepository.findById(contestId)
            .orElseThrow { IllegalArgumentException("Contest $contestId not found") }

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
            .let { return contestRepository.getAllActiveContestsSignedUp(it!!.id!!) }

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