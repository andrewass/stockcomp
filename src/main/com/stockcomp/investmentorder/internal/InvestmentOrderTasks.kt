package com.stockcomp.investmentorder.internal

import com.stockcomp.participant.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentOrderTasks(
    private val investmentOrderTaskService: InvestmentOrderTaskService,
    private val participantService: ParticipantService,
) {
    private val logger = LoggerFactory.getLogger(InvestmentOrderTasks::class.java)

    @Scheduled(fixedRate = 15000)
    fun runMaintainInvestmentOrders() {
        try {
            participantService.getAllActiveParticipants()
                .forEach { investmentOrderTaskService.processInvestmentOrders(it.id!!) }
        } catch (e: Exception) {
            logger.error("Failed order processing : ${e.message}")
        }
    }


}