package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.ScheduledJobInstrumentation
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentOrderTasks(
    private val investmentOrderMaintenanceService: InvestmentOrderMaintenanceService,
    private val scheduledJobInstrumentation: ScheduledJobInstrumentation,
) {
    @Scheduled(fixedRateString = "\${scheduling.tasks.investment-order.maintain-investment-orders.fixed-rate-ms}")
    @SchedulerLock(name = "lockForMaintainInvestmentOrders")
    fun runMaintainInvestmentOrders() {
        scheduledJobInstrumentation.record(JOB_NAME) {
            investmentOrderMaintenanceService.maintainInvestmentOrders()
        }
    }

    private companion object {
        const val JOB_NAME = "investment-order-maintain-orders"
    }
}
