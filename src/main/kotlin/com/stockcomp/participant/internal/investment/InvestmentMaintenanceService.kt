package com.stockcomp.participant.internal.investment

import com.stockcomp.common.ScheduledJobRunResult
import com.stockcomp.configuration.InvestmentMaintenanceProperties
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.internal.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InvestmentMaintenanceService(
    private val participantService: ParticipantService,
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
            var attemptedItems = 0

            contestService.getActiveContests().forEach contestLoop@{ contest ->
                val participants = participantService.getAllByContest(contest.contestId)
                if (attemptedItems >= maxParticipantsPerRun) {
                    skippedItems += participants.size
                    return@contestLoop
                }
                participants.forEachIndexed { index, participant ->
                    if (attemptedItems >= maxParticipantsPerRun) {
                        skippedItems += participants.size - index
                        return@contestLoop
                    }

                    attemptedItems += 1
                    try {
                        val participantId =
                            requireNotNull(participant.participantId) {
                                "Participant id is null while maintaining investments for contest ${contest.contestId}"
                            }
                        investmentProcessingService.maintainInvestments(participantId)
                        processedItems += 1
                    } catch (e: Exception) {
                        failedItems += 1
                        logger.error(
                            "scheduled_job_item_failure job={} action=maintain_investments contestId={} participantId={}",
                            JOB_NAME,
                            contest.contestId,
                            participant.participantId,
                            e,
                        )
                    }
                }
            }
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
