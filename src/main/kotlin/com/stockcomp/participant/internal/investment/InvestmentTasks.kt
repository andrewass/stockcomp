package com.stockcomp.participant.internal.investment

import com.stockcomp.common.ScheduledJobInstrumentation
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentTasks(
    private val investmentMaintenanceService: InvestmentMaintenanceService,
    private val scheduledJobInstrumentation: ScheduledJobInstrumentation,
) {
    @Scheduled(fixedRateString = "\${scheduling.tasks.investment.maintain-investments.fixed-rate-ms}")
    @SchedulerLock(name = "lockForMaintainInvestments")
    fun runMaintainInvestments() {
        scheduledJobInstrumentation.record(JOB_NAME) {
            investmentMaintenanceService.maintainInvestments()
        }
    }

    private companion object {
        const val JOB_NAME = "investment-maintain-investments"
    }
}
