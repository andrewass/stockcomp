package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ContestService(
    private val contestRepository: ContestRepository,
    private val userService: UserService,
) {

    fun createContest(request: CreateContestRequest) {
        Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime,
            endTime = request.startTime.plusMonths(2)
        ).also { contestRepository.save(it) }
    }

    fun deleteContest(contestNumber: Int) {
        contestRepository.deleteByContestNumber(contestNumber)
    }

    fun updateContest(request: UpdateContestRequest) {
        contestRepository.findByContestNumber(request.contestNumber).apply {
            contestStatus = request.contestStatus
            startTime = request.startTime
            endTime = request.startTime.plusMonths(2)
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
            listOf(ContestStatus.RUNNING, ContestStatus.STOPPED, ContestStatus.AWAITING_START)
        )

    fun getRunningContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(listOf(ContestStatus.RUNNING))

    fun getCompletedContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(listOf(ContestStatus.COMPLETED))

    fun getAllContestsSorted(pageNumber: Int, pageSize: Int): Page<Contest> =
        contestRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("contestNumber")))

    fun findByContestNumber(contestNumber: Int): Contest =
        contestRepository.findByContestNumber(contestNumber)

    fun saveContest(contest: Contest) {
        contestRepository.save(contest)
    }
}