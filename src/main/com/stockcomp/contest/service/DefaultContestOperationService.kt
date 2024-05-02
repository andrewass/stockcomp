package com.stockcomp.contest.service

import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.domain.LeaderboardUpdateStatus
import com.stockcomp.leaderboard.service.LeaderboardOperationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DefaultContestOperationService(
    private val contestService: ContestService,
    private val leaderboardOperationService: LeaderboardOperationService,
) : ContestOperationService {

    private val logger = LoggerFactory.getLogger(DefaultContestOperationService::class.java)

    override fun updateLeaderboard() {
        contestService.getCompletedContests()
            .filter { it.leaderboardUpdateStatus == LeaderboardUpdateStatus.AWAITING }
            .forEach {
                leaderboardOperationService.updateLeaderboardEntries(it)
                it.leaderboardUpdateStatus = LeaderboardUpdateStatus.COMPLETED
                contestService.saveContest(it)
            }
    }

    override fun maintainContestStatus() {
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
