package com.stockcomp.contest

import com.stockcomp.contest.internal.ContestServiceInternal
import org.springframework.stereotype.Service

@Service
class ContestServiceExternal(
    private val contestService: ContestServiceInternal
) {
    fun getContest(contestId: Long): ContestDto =
        toContestDto(contestService.getContest(contestId))

    fun getRunningContests(): List<ContestDto> =
        contestService.getRunningContests().map { toContestDto(it) }

    fun getActiveContests(): List<ContestDto> =
        contestService.getActiveContests().map { toContestDto(it) }

    fun isCompletedContest(contestId: Long): Boolean =
        contestService.getContest(contestId).isCompleted()
}
