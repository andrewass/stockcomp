package com.stockcomp.investment.task

import com.stockcomp.investment.service.InvestmentTaskService
import com.stockcomp.participant.service.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentTasks(
    private val participantService: ParticipantService,
    private val investmentTaskService: InvestmentTaskService,
) {
    private val logger = LoggerFactory.getLogger(InvestmentTasks::class.java)

    @Scheduled(fixedRate = 15000)
    fun runMaintainInvestments() {
        try {
            participantService.getAllActiveParticipants()
                .forEach { investmentTaskService.maintainInvestments(it.id!!) }
        } catch (e: Exception) {
            logger.error("Failed maintain investments : ${e.message}")
        }
    }
}