package com.stockcomp.contest.service

import com.stockcomp.investment.service.InvestmentProcessService
import com.stockcomp.investmentorder.service.InvestmentOrderProcessService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultContestOperationService(
    private val investmentOrderProcessService: InvestmentOrderProcessService,
    private val investmentProcessService: InvestmentProcessService
) : ContestOperationService {

    private val logger = LoggerFactory.getLogger(DefaultContestOperationService::class.java)

    override fun updateLeaderboard() {
        TODO("Not yet implemented")
    }

    override fun maintainContestStatus() {
        TODO("Not yet implemented")
    }

    override fun maintainInvestments() {
        investmentProcessService.maintainInvestments()
    }

    override fun processInvestmentOrders() {
        investmentOrderProcessService.processInvestmentOrders()
    }


}