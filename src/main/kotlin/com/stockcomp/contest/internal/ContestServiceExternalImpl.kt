package com.stockcomp.contest.internal

import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestServiceExternal
import org.springframework.stereotype.Service

@Service
class ContestServiceExternalImpl(
    private val contestService: ContestService,
) : ContestServiceExternal {
    override fun getContest(contestId: Long): ContestDto = toContestDto(contestService.getContest(contestId))

    override fun getRunningContests(): List<ContestDto> = contestService.getRunningContests().map { toContestDto(it) }

    override fun getActiveContests(): List<ContestDto> = contestService.getActiveContests().map { toContestDto(it) }

    override fun getContestsAwaitingCompletion(): List<ContestDto> = contestService.getContestsAwaitingCompletion().map { toContestDto(it) }

    override fun isCompletedContest(contestId: Long): Boolean = contestService.getContest(contestId).isCompleted()

    override fun markContestAsCompleted(contestId: Long) {
        contestService.markContestAsCompleted(contestId)
    }

    override fun lockContestForCompletion(contestId: Long): Boolean = contestService.lockContestForCompletion(contestId)
}
