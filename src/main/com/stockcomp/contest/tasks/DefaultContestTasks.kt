package com.stockcomp.contest.tasks

import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.domain.contest.enums.LeaderboardUpdateStatus
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.investmentorder.service.ProcessOrdersService
import com.stockcomp.leaderboard.service.LeaderboardService
import com.stockcomp.participant.service.MaintainParticipantService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultContestTasks(
    private val contestRepository: ContestRepository,
    private val maintainParticipantService: MaintainParticipantService,
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

            val contest = withContext(Dispatchers.IO) {
                contestRepository.findByContestNumber(contestNumber)
            }
            investmentOrderService.terminateRemainingOrders(contest)
            if (contest.leaderboardUpdateStatus != LeaderboardUpdateStatus.COMPLETED){
                leaderboardService.updateLeaderboard(contest)
                contest.leaderboardUpdateStatus = LeaderboardUpdateStatus.COMPLETED
                withContext(Dispatchers.IO) {
                    contestRepository.save(contest)
                }
            }
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
                    maintainParticipantService.maintainParticipants()
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