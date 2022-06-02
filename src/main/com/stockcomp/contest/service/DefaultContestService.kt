package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultContestService(
    private val contestRepository: ContestRepository,
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

    override fun getContests(statusList: List<ContestStatus>): List<Contest> =
        if (statusList.isEmpty()) {
            contestRepository.findAll()
        } else {
            contestRepository.findAllByContestStatusIn(statusList)
        }

    override fun findByContestNumber(contestNumber: Int): Contest =
        contestRepository.findByContestNumber(contestNumber)


    override fun findByContestNumberAndStatus(status: ContestStatus, contestNumber: Int): Contest =
        contestRepository.findByContestNumberAndContestStatus(contestNumber, status)


    override fun saveContest(contest: Contest) {
        contestRepository.save(contest)
    }
}