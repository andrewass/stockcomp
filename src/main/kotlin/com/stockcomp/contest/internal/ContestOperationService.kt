package com.stockcomp.contest.internal

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ContestOperationService(
    private val contestService: ContestServiceInternal
) {
    private val logger = LoggerFactory.getLogger(ContestOperationService::class.java)

    fun maintainContestStatus() {
        contestService.getActiveContests()
            .forEach {
                if (it.shouldStartContest()) {
                    logger.info("Starting contest ${it.contestId}")
                    it.startContest()
                    contestService.saveContest(it)
                }
                if (it.shouldStopFinishedContest()) {
                    logger.info("Stopping finished contest ${it.contestId}")
                    it.stopFinishedContest()
                    contestService.saveContest(it)
                }
            }
    }
}