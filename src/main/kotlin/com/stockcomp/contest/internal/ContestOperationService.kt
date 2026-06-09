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
    fun maintainContestStatus(): Int {
        val now = LocalDateTime.now(clock)
        val activeContests = contestService.getActiveContests()
        activeContests
            .forEach {
                if (it.shouldStartContest(now)) {
                    logger.info("scheduled_job_item job={} action=start_contest contestId={}", JOB_NAME, it.contestId)
                    it.startContest()
                }
                if (it.shouldStopFinishedContest(now)) {
                    logger.info("scheduled_job_item job={} action=stop_contest contestId={}", JOB_NAME, it.contestId)
                    it.stopFinishedContest()
                }
            }
        return activeContests.size
    }

    private companion object {
        const val JOB_NAME = "contest-maintain-contests"
    }
}
