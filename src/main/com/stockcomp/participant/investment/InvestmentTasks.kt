package com.stockcomp.participant.investment

import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentTasks(
    private val participantService: ParticipantService,
    private val contestService: ContestServiceExternal,
    private val investmentTaskService: InvestmentTaskService,
) {
    private val logger = LoggerFactory.getLogger(InvestmentTasks::class.java)

    @Scheduled(fixedRate = 15000)
    fun runMaintainInvestments() {
        try {
            contestService.getActiveContests()
                .forEach { contestId ->
                    participantService.getAllByContest(contestId)
                        .forEach { participant ->
                            investmentTaskService.maintainInvestments(participant.participantId!!)
                        }
                }
        } catch (e: Exception) {
            logger.error("Failed maintain investments : ${e.message}")
        }
    }
}