package com.stockcomp.leaderboard.internal.job

import com.stockcomp.common.ScheduledJobInstrumentation
import com.stockcomp.common.ScheduledJobRunResult
import com.stockcomp.contest.ContestServiceExternal
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class LeaderboardJobScheduler(
    private val leaderboardJobRepository: LeaderboardJobRepository,
    private val leaderboardJobProcessService: LeaderboardJobProcessService,
    private val contestService: ContestServiceExternal,
    private val scheduledJobInstrumentation: ScheduledJobInstrumentation,
) {
    private val openStatuses = listOf(JobStatus.CREATED, JobStatus.FAILED)

    @Transactional
    @Scheduled(fixedDelayString = "\${scheduling.tasks.leaderboard.process-jobs.fixed-delay-ms}")
    @SchedulerLock(name = "lockForProcessLeaderboardJob")
    fun processLeaderboardJob() {
        scheduledJobInstrumentation.record(PROCESS_JOB_NAME) {
            leaderboardJobRepository
                .findFirstByJobStatusInAndNextRunAtLessThanEqualOrderByNextRunAtAsc(
                    jobStatuses = openStatuses,
                    timeLimit = LocalDateTime.now(),
                )?.let {
                    when (leaderboardJobProcessService.processJob(it)) {
                        JobStatus.COMPLETED -> ScheduledJobRunResult.success(processedItems = 1)
                        JobStatus.FAILED -> ScheduledJobRunResult.failure(processedItems = 1)
                        JobStatus.CREATED -> ScheduledJobRunResult.success(processedItems = 1)
                    }
                } ?: ScheduledJobRunResult.skipped()
        }
    }

    @Transactional
    @Scheduled(fixedDelayString = "\${scheduling.tasks.leaderboard.create-jobs.fixed-delay-ms}")
    @SchedulerLock(name = "lockForCreateLeaderboardJob")
    fun createLeaderboardJobs() {
        scheduledJobInstrumentation.record(CREATE_JOB_NAME) {
            var createdItems = 0
            var skippedItems = 0
            contestService.getContestsAwaitingCompletion().forEach { contest ->
                if (!leaderboardJobRepository.existsByContestIdAndJobStatusIn(contest.contestId, openStatuses)) {
                    leaderboardJobRepository.save(LeaderboardJob(contestId = contest.contestId))
                    createdItems += 1
                } else {
                    skippedItems += 1
                }
            }
            if (createdItems == 0) {
                ScheduledJobRunResult.skipped(skippedItems = skippedItems.coerceAtLeast(1))
            } else {
                ScheduledJobRunResult.success(
                    processedItems = createdItems,
                    skippedItems = skippedItems,
                )
            }
        }
    }

    private companion object {
        const val PROCESS_JOB_NAME = "leaderboard-process-jobs"
        const val CREATE_JOB_NAME = "leaderboard-create-jobs"
    }
}
