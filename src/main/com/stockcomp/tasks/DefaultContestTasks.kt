package com.stockcomp.tasks

import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.service.investment.MaintainInvestmentService
import com.stockcomp.service.leaderboard.LeaderboardService
import com.stockcomp.service.order.InvestmentOrderService
import com.stockcomp.service.order.ProcessOrdersService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultContestTasks(
    private val contestRepository: ContestRepository,
    private val maintainInvestmentService: MaintainInvestmentService,
    private val processOrdersService: ProcessOrdersService,
    private val investmentOrderService: InvestmentOrderService,
    private val leaderboardService: LeaderboardService
) : ContestTasks {

    private val logger = LoggerFactory.getLogger(DefaultContestTasks::class.java)

    private var orderJob: Job? = null
    private var investmentJob: Job? = null

    override fun startContestTasks() {
        startOrderProcessing()
        startMaintainInvestments()
    }

    override fun stopContestTasks() {
        stopOrderProcessing()
        stopMaintainInvestments()
    }

    override fun completeContestTasks(contestNumber: Int) {
        CoroutineScope(Default).launch {
            completeOrderProcessing()
            completeMaintainInvestments()
            contestRepository.findByContestNumber(contestNumber)
                .also { investmentOrderService.terminateRemainingOrders(it) }
                .also { leaderboardService.updateLeaderboard(it) }
        }
    }

    override fun startOrderProcessing() {
        assertJobNotAlreadyActive(orderJob)

        if (existsRunningContest()) {
            orderJob = CoroutineScope(Default).launch {
                logger.info("Starting maintenance of investment orders")
                while (isActive) {
                    processOrdersService.processInvestmentOrders()
                    delay(15000L)
                }
            }
        }
    }

    override fun stopOrderProcessing() {
        orderJob?.cancel()
        orderJob = null
        logger.info("Stopping processing of investment orders")
    }

    override fun startMaintainInvestments() {
        assertJobNotAlreadyActive(investmentJob)

        if (existsRunningContest()) {
            investmentJob = CoroutineScope(Default).launch {
                logger.info("Starting maintenance of investment returns")
                while (isActive) {
                    maintainInvestmentService.maintainInvestments()
                    delay(15000L)
                }
            }
        }
    }

    override fun stopMaintainInvestments() {
        investmentJob?.cancel()
        investmentJob = null
        logger.info("Stopping maintenance of investment returns")
    }

    private suspend fun completeOrderProcessing() {
        orderJob?.also { it.cancelAndJoin() }
    }

    private suspend fun completeMaintainInvestments() {
        investmentJob?.also { it.cancelAndJoin() }
    }

    private fun assertJobNotAlreadyActive(job: Job?) {
        if (job?.isActive == true) {
            throw IllegalStateException("Cannot start already running job")
        }
    }

    private fun existsRunningContest(): Boolean =
        contestRepository.findAllByContestStatus(ContestStatus.RUNNING).isNotEmpty()
}