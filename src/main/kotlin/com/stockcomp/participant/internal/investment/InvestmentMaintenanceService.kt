package com.stockcomp.participant.internal.investment

import com.stockcomp.common.ScheduledJobRunResult
import com.stockcomp.configuration.InvestmentMaintenanceProperties
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.internal.ParticipantMaintenanceBatchService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InvestmentMaintenanceService(
    private val participantMaintenanceBatchService: ParticipantMaintenanceBatchService,
    private val contestService: ContestServiceExternal,
    private val investmentProcessingService: InvestmentProcessingService,
    private val investmentMaintenanceProperties: InvestmentMaintenanceProperties,
) {
    private val logger = LoggerFactory.getLogger(InvestmentMaintenanceService::class.java)

    fun maintainInvestments(): ScheduledJobRunResult {
        var processedItems = 0
        var failedItems = 0
        var skippedItems = 0
        return try {
            val maxParticipantsPerRun = investmentMaintenanceProperties.maxParticipantsPerRun
            val participantIds =
                participantMaintenanceBatchService.getNextParticipantIds(
                    jobName = JOB_NAME,
                    contestIds = contestService.getRunningContests().map { it.contestId },
                    maxParticipants = maxParticipantsPerRun,
                )

            participantIds.forEach { participantId ->
                try {
                    investmentProcessingService.maintainInvestments(participantId)
                    processedItems += 1
                } catch (e: Exception) {
                    failedItems += 1
                    logger.error(
                        "scheduled_job_item_failure job={} action=maintain_investments participantId={}",
                        JOB_NAME,
                        participantId,
                        e,
                    )
                }
            }
            participantIds.lastOrNull()?.let { participantMaintenanceBatchService.advanceCursor(JOB_NAME, it) }
            ScheduledJobRunResult.fromItemCounts(
                processedItems = processedItems,
                failedItems = failedItems,
                skippedItems = skippedItems,
            )
        } catch (e: Exception) {
            logger.error("scheduled_job_failure job={} action=maintain_investments", JOB_NAME, e)
            ScheduledJobRunResult.failure(
                processedItems = processedItems,
                failedItems = failedItems,
                skippedItems = skippedItems,
            )
        }
    }

    private companion object {
        const val JOB_NAME = "investment-maintain-investments"
    }
}
