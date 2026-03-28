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

    fun processJob(job: LeaderboardJob) {
        try {
            leaderboardService.updateLeaderboard(job.contestId)
            leaderboardJobStateService.markAsCompleted(job)
        } catch (e: Exception) {
            logger.error("Failed to process leaderboard job {}", job.leaderboardJobId, e)
            leaderboardJobStateService.markAsFailed(job)
        }
    }
}
