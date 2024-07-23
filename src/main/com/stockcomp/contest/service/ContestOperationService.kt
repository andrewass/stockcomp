package com.stockcomp.contest.service

import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.domain.LeaderboardUpdateStatus
import com.stockcomp.leaderboard.service.LeaderboardOperationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ContestOperationService(
    private val contestService: ContestServiceInternal,
    private val leaderboardOperationService: LeaderboardOperationService,
) {
    private val logger = LoggerFactory.getLogger(ContestOperationService::class.java)

    fun updateLeaderboard() {
        contestService.getCompletedContests()
            .filter { it.leaderboardUpdateStatus == LeaderboardUpdateStatus.AWAITING }
            .forEach {
                leaderboardOperationService.updateLeaderboardEntries(it)
                it.leaderboardUpdateStatus = LeaderboardUpdateStatus.COMPLETED
                contestService.saveContest(it)
            }
    }

    fun maintainContestStatus() {
        contestService.getActiveContests()
            .forEach {
                if (it.contestStatus == ContestStatus.AWAITING_START && it.startTime.isBefore(LocalDateTime.now())) {
                    logger.info("Changing contest status to RUNNING for contest ${it.contestNumber}")
                    it.contestStatus = ContestStatus.RUNNING
                    contestService.saveContest(it)
                }
                if (it.endTime.isBefore(LocalDateTime.now())) {
                    logger.info("Changing contest status to COMPLETED for contest ${it.contestNumber}")
                    it.contestStatus = ContestStatus.COMPLETED
                    contestService.saveContest(it)
                }
            }
    }
}