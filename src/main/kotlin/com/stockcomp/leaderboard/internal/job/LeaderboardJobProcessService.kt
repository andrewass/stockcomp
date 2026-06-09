package com.stockcomp.leaderboard.internal.job

import com.stockcomp.leaderboard.internal.LeaderboardService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LeaderboardJobProcessService(
    private val leaderboardJobStateService: LeaderboardJobStateService,
    private val leaderboardService: LeaderboardService,
) {
    private val logger = LoggerFactory.getLogger(LeaderboardJobProcessService::class.java)

    fun processJob(job: LeaderboardJob): JobStatus =
        try {
            leaderboardService.updateLeaderboard(job.contestId)
            leaderboardJobStateService.markAsCompleted(job)
            JobStatus.COMPLETED
        } catch (e: Exception) {
            logger.error(
                "scheduled_job_item_failure job={} action=process_leaderboard_job leaderboardJobId={} contestId={} attempts={}",
                JOB_NAME,
                job.leaderboardJobId,
                job.contestId,
                job.attempts(),
                e,
            )
            leaderboardJobStateService.markAsFailed(job)
            JobStatus.FAILED
        }

    private companion object {
        const val JOB_NAME = "leaderboard-process-jobs"
    }
}
