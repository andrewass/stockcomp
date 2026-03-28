package com.stockcomp.contest.internal

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime

@Service
class ContestOperationService(
    private val contestService: ContestService,
    private val clock: Clock,
) {
    private val logger = LoggerFactory.getLogger(ContestOperationService::class.java)

    @Transactional
    fun maintainContestStatus() {
        val now = LocalDateTime.now(clock)
        contestService
            .getActiveContests()
            .forEach {
                if (it.shouldStartContest(now)) {
                    logger.info("Starting contest ${it.contestId}")
                    it.startContest()
                }
                if (it.shouldStopFinishedContest(now)) {
                    logger.info("Stopping finished contest ${it.contestId}")
                    it.stopFinishedContest()
                }
            }
    }
}
