package com.stockcomp.contest.internal

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContestOperationService(
    private val contestService: ContestService,
) {
    private val logger = LoggerFactory.getLogger(ContestOperationService::class.java)

    @Transactional
    fun maintainContestStatus() {
        contestService.getActiveContests()
            .forEach {
                if (it.shouldStartContest()) {
                    logger.info("Starting contest ${it.contestId}")
                    it.startContest()
                }
                if (it.shouldStopFinishedContest()) {
                    logger.info("Stopping finished contest ${it.contestId}")
                    it.stopFinishedContest()
                }
            }
    }
}