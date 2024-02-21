package com.stockcomp.investment.task

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentTasks {

    @Scheduled(fixedRate = 30000)
    fun runMaintainInvestments() {
        println("placeholder investments")
    }
}