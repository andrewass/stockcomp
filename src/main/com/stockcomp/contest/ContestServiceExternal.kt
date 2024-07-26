package com.stockcomp.contest

import com.stockcomp.contest.internal.ContestStatus
import com.stockcomp.contest.internal.ContestServiceInternal
import org.springframework.stereotype.Service

@Service
class ContestServiceExternal(
    private val contestService: ContestServiceInternal
) {

    fun getRunningContests(): List<Long> =
        contestService.getRunningContests().map { it.contestId!! }

    fun getActiveContests(): List<Long> =
        contestService.getActiveContests().map { it.contestId!! }

    fun isCompletedContest(contestId: Long) =
        contestService.getContest(contestId)
            .contestStatus === ContestStatus.COMPLETED
}