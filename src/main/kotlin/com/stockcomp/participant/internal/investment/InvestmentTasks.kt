package com.stockcomp.participant.internal.investment

import com.stockcomp.common.ScheduledJobInstrumentation
import com.stockcomp.common.ScheduledJobRunResult
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.internal.ParticipantService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentTasks(
    private val participantService: ParticipantService,
    private val contestService: ContestServiceExternal,
    private val investmentProcessingService: InvestmentProcessingService,
    private val scheduledJobInstrumentation: ScheduledJobInstrumentation,
) {
    private val logger = LoggerFactory.getLogger(InvestmentTasks::class.java)

    @Scheduled(fixedRateString = "\${scheduling.tasks.investment.maintain-investments.fixed-rate-ms}")
    @SchedulerLock(name = "lockForMaintainInvestments")
    fun runMaintainInvestments() {
        scheduledJobInstrumentation.record(JOB_NAME) {
            var processedItems = 0
            try {
                contestService
                    .getActiveContests()
                    .forEach { contest ->
                        participantService
                            .getAllByContest(contest.contestId)
                            .forEach { participant ->
                                val participantId =
                                    requireNotNull(participant.participantId) {
                                        "Participant id is null while maintaining investments for contest ${contest.contestId}"
                                    }
                                investmentProcessingService.maintainInvestments(participantId)
                                processedItems += 1
                            }
                    }
                if (processedItems == 0) {
                    ScheduledJobRunResult.skipped()
                } else {
                    ScheduledJobRunResult.success(processedItems = processedItems)
                }
            } catch (e: Exception) {
                logger.error("Failed maintain investments", e)
                ScheduledJobRunResult.failure(processedItems = processedItems)
            }
        }
    }

    private companion object {
        const val JOB_NAME = "investment-maintain-investments"
    }
}
