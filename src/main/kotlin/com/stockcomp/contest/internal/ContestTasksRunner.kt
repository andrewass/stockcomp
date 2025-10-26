package com.stockcomp.contest.internal

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ContestTasksRunner(
    private val contestOperationService: ContestOperationService,
) {
    @Scheduled(fixedRate = 30000)
    @SchedulerLock(name = "lockForMaintainContests")
    fun runMaintainContests() {
        contestOperationService.maintainContestStatus()
    }
}
