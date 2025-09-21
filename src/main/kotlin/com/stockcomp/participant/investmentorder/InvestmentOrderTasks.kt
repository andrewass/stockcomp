package com.stockcomp.participant.investmentorder

import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentOrderTasks(
    private val investmentOrderProcessingService: com.stockcomp.participant.investmentorder.InvestmentOrderProcessingService,
    private val participantService: ParticipantService,
    private val contestService: ContestServiceExternal
) {
    private val logger = LoggerFactory.getLogger(com.stockcomp.participant.investmentorder.InvestmentOrderTasks::class.java)

    @Scheduled(fixedRate = 15000)
    fun runMaintainInvestmentOrders() {
        try {
            contestService.getActiveContests()
                .forEach {
                    participantService.getAllByContest(it.contestId)
                        .forEach { participant ->
                            investmentOrderProcessingService.processInvestmentOrders(participant.participantId!!)
                        }
                }
        } catch (e: Exception) {
            logger.error("Failed order processing : ${e.message}")
        }
    }
}