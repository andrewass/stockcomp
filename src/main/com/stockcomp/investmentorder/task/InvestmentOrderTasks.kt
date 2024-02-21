package com.stockcomp.investmentorder.task

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentOrderTasks {

    @Scheduled(fixedRate = 30000)
    fun runMaintainInvestmentOrders() {
        println("placeholder investmentOrders")
    }
}