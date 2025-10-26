package com.stockcomp.participant.internal.investment

import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.internal.ParticipantService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentTasks(
    private val participantService: ParticipantService,
    private val contestService: ContestServiceExternal,
    private val investmentProcessingService: InvestmentProcessingService,
) {
    private val logger = LoggerFactory.getLogger(InvestmentTasks::class.java)

    @Scheduled(fixedRate = 5000)
    @SchedulerLock(name = "lockForMaintainInvestments")
    fun runMaintainInvestments() {
        try {
            contestService
                .getActiveContests()
                .forEach { contest ->
                    participantService
                        .getAllByContest(contest.contestId)
                        .forEach { participant ->
                            investmentProcessingService.maintainInvestments(participant.participantId!!)
                        }
                }
        } catch (e: Exception) {
            logger.error("Failed maintain investments : ${e.message}")
        }
    }
}
