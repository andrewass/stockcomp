package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultContestService(
    private val contestRepository: ContestRepository
) : ContestService {

    override fun createContest(request: CreateContestRequest) {
        Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime,
            endTime = request.startTime.plusMonths(2)
        ).also { contestRepository.save(it) }
    }

    override fun deleteContest(contestNumber: Int) {
        contestRepository.deleteByContestNumber(contestNumber)
    }

    override fun updateContest(request: UpdateContestRequest) {
        contestRepository.findByContestNumber(request.contestNumber).apply {
            contestStatus = request.contestStatus
            startTime = request.startTime
            endTime = request.startTime.plusMonths(2)
        }.also { contestRepository.save(it) }
    }

    override fun getActiveContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(
            listOf(ContestStatus.RUNNING, ContestStatus.STOPPED, ContestStatus.AWAITING_START)
        )

    override fun getRunningContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(listOf(ContestStatus.RUNNING))

    override fun getCompletedContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(listOf(ContestStatus.COMPLETED))

    override fun getAllContestsSorted(pageNumber: Int, pageSize: Int): Page<Contest> =
        contestRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("contestNumber")))

    override fun findByContestNumber(contestNumber: Int): Contest =
        contestRepository.findByContestNumber(contestNumber)

    override fun findByContestNumberAndStatus(status: ContestStatus, contestNumber: Int): Contest =
        contestRepository.findByContestNumberAndContestStatus(contestNumber, status)

    override fun saveContest(contest: Contest) {
        contestRepository.save(contest)
    }
}