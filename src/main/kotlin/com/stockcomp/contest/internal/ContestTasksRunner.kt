package com.stockcomp.contest.internal

import com.stockcomp.common.ScheduledJobInstrumentation
import com.stockcomp.common.ScheduledJobRunResult
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ContestTasksRunner(
    private val contestOperationService: ContestOperationService,
    private val scheduledJobInstrumentation: ScheduledJobInstrumentation,
) {
    @Scheduled(fixedRateString = "\${scheduling.tasks.contest.maintain-contests.fixed-rate-ms}")
    @SchedulerLock(name = "lockForMaintainContests")
    fun runMaintainContests() {
        scheduledJobInstrumentation.record(JOB_NAME) {
            val processedItems = contestOperationService.maintainContestStatus()
            if (processedItems == 0) {
                ScheduledJobRunResult.skipped()
            } else {
                ScheduledJobRunResult.success(processedItems = processedItems)
            }
        }
    }

    private companion object {
        const val JOB_NAME = "contest-maintain-contests"
    }
}
