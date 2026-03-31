package com.stockcomp.participant.internal.investmentorder

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
) {
    private val logger = LoggerFactory.getLogger(InvestmentOrderTasks::class.java)

    @Scheduled(fixedRate = 5000)
    @SchedulerLock(name = "lockForMaintainInvestmentOrders")
    fun runMaintainInvestmentOrders() {
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
                        }
                }
        } catch (e: Exception) {
            logger.error("Failed order processing", e)
        }
    }
}
