package com.stockcomp.contest.service

import com.stockcomp.investmentorder.service.InvestmentOrderProcessService
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.leaderboard.service.LeaderboardService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultContestOperationService(
    private val orderProcessService: InvestmentOrderProcessService,
    private val investmentOrderService: InvestmentOrderService,
    private val leaderboardService: LeaderboardService,
    private val contestService: ContestService
) : ContestOperationService {

    private val logger = LoggerFactory.getLogger(DefaultContestOperationService::class.java)

    override fun maintainContestStatus() {
        TODO("Not yet implemented")
    }

    override fun maintainInvestments() {
        TODO("Not yet implemented")
    }

    override fun processInvestmentOrders() {
        TODO("Not yet implemented")
    }

}