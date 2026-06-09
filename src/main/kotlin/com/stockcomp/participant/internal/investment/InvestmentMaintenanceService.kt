package com.stockcomp.participant.internal.investment

import com.stockcomp.common.ScheduledJobRunResult
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.internal.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InvestmentMaintenanceService(
    private val participantService: ParticipantService,
    private val contestService: ContestServiceExternal,
    private val investmentProcessingService: InvestmentProcessingService,
) {
    private val logger = LoggerFactory.getLogger(InvestmentMaintenanceService::class.java)

    fun maintainInvestments(): ScheduledJobRunResult {
        var processedItems = 0
        return try {
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
