package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.ScheduledJobRunResult
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.internal.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InvestmentOrderMaintenanceService(
    private val investmentOrderProcessingService: InvestmentOrderProcessingService,
    private val participantService: ParticipantService,
    private val contestService: ContestServiceExternal,
) {
    private val logger = LoggerFactory.getLogger(InvestmentOrderMaintenanceService::class.java)

    fun maintainInvestmentOrders(): ScheduledJobRunResult {
        var processedItems = 0
        var failedItems = 0
        return try {
            contestService
                .getActiveContests()
                .forEach {
                    participantService
                        .getAllByContest(it.contestId)
                        .forEach { participant ->
                            try {
                                val participantId =
                                    requireNotNull(participant.participantId) {
                                        "Participant id is null while processing investment orders for contest ${it.contestId}"
                                    }
                                investmentOrderProcessingService.processInvestmentOrders(participantId)
                                processedItems += 1
                            } catch (e: Exception) {
                                failedItems += 1
                                logger.error(
                                    "Failed order processing for contest {} participant {}",
                                    it.contestId,
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
            logger.error("Failed order processing", e)
            ScheduledJobRunResult.failure(
                processedItems = processedItems,
                failedItems = failedItems,
            )
        }
    }
}
