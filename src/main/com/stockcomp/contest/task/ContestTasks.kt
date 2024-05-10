package com.stockcomp.contest.task

import com.stockcomp.contest.service.ContestOperationService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ContestTasks(
    private val contestOperationService: ContestOperationService
) {

    @Scheduled(fixedRate = 30000)
    fun runMaintainContests() {
        contestOperationService.maintainContestStatus()
    }
}