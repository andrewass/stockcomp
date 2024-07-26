package com.stockcomp.contest.internal

import com.stockcomp.leaderboard.LeaderboardServiceExternal
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ContestOperationService(
    private val contestService: ContestServiceInternal
) {
    private val logger = LoggerFactory.getLogger(ContestOperationService::class.java)

    fun maintainContestStatus() {
        contestService.getActiveContests()
            .forEach {
                if (it.contestStatus == ContestStatus.AWAITING_START && it.startTime.isBefore(LocalDateTime.now())) {
                    logger.info("Changing contest status to RUNNING for contest ${it.contestId}")
                    it.contestStatus = ContestStatus.RUNNING
                    contestService.saveContest(it)
                }
                if (it.endTime.isBefore(LocalDateTime.now())) {
                    logger.info("Changing contest status to COMPLETED for contest ${it.contestId}")
                    it.contestStatus = ContestStatus.COMPLETED
                    contestService.saveContest(it)
                }
            }
    }
}