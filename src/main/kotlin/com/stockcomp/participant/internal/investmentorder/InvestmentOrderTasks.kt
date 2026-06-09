package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.ScheduledJobInstrumentation
import com.stockcomp.common.ScheduledJobRunResult
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.internal.ParticipantService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentOrderTasks(
    private val investmentOrderProcessingService: InvestmentOrderProcessingService,
    private val participantService: ParticipantService,
    private val contestService: ContestServiceExternal,
    private val scheduledJobInstrumentation: ScheduledJobInstrumentation,
) {
    private val logger = LoggerFactory.getLogger(InvestmentOrderTasks::class.java)

    @Scheduled(fixedRateString = "\${scheduling.tasks.investment-order.maintain-investment-orders.fixed-rate-ms}")
    @SchedulerLock(name = "lockForMaintainInvestmentOrders")
    fun runMaintainInvestmentOrders() {
        scheduledJobInstrumentation.record(JOB_NAME) {
            var processedItems = 0
            try {
                contestService
                    .getActiveContests()
                    .forEach {
                        participantService
                            .getAllByContest(it.contestId)
                            .forEach { participant ->
                                val participantId =
                                    requireNotNull(participant.participantId) {
                                        "Participant id is null while processing investment orders for contest ${it.contestId}"
                                    }
                                investmentOrderProcessingService.processInvestmentOrders(participantId)
                                processedItems += 1
                            }
                    }
                if (processedItems == 0) {
                    ScheduledJobRunResult.skipped()
                } else {
                    ScheduledJobRunResult.success(processedItems = processedItems)
                }
            } catch (e: Exception) {
                logger.error("Failed order processing", e)
                ScheduledJobRunResult.failure(processedItems = processedItems)
            }
        }
    }

    private companion object {
        const val JOB_NAME = "investment-order-maintain-orders"
    }
}
