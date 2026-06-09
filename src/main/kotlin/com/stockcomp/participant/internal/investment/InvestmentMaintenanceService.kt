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
        var failedItems = 0
        return try {
            contestService
                .getActiveContests()
                .forEach { contest ->
                    participantService
                        .getAllByContest(contest.contestId)
                        .forEach { participant ->
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
                                    "Failed maintain investments for contest {} participant {}",
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
            )
        } catch (e: Exception) {
            logger.error("Failed maintain investments", e)
            ScheduledJobRunResult.failure(
                processedItems = processedItems,
                failedItems = failedItems,
            )
        }
    }
}
