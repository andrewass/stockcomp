package com.stockcomp.contest.internal

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ContestTasksRunner(
    private val contestOperationService: ContestOperationService
) {

    @Scheduled(fixedRate = 30000)
    fun runMaintainContests() {
        contestOperationService.maintainContestStatus()
    }
}