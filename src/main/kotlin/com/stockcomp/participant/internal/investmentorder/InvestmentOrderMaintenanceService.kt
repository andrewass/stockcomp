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
        return try {
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
