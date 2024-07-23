package com.stockcomp.contest

import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.service.ContestServiceInternal
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