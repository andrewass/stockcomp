package com.stockcomp.contest.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.entity.LeaderboardUpdateStatus
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.investmentorder.service.InvestmentOrderProcessService
import com.stockcomp.leaderboard.service.LeaderboardService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultContestOperationService(
    private val orderProcessService: InvestmentOrderProcessService,
    private val investmentOrderService: InvestmentOrderService,
    private val leaderboardService: LeaderboardService,
    private val contestService: ContestService
) : ContestMaintenanceService {

    private val logger = LoggerFactory.getLogger(DefaultContestOperationService::class.java)

    private var orderJob: Job? = null
    private var investmentJob: Job? = null

    override fun startContest(contestNumber: Int) {
        contestService.findByContestNumberAndStatus(ContestStatus.AWAITING_START, contestNumber).also {
            it.contestStatus = ContestStatus.RUNNING
            contestService.saveContest(it)
            startOrderProcessing()
            startMaintainInvestments()
            logger.info("Starting contest $contestNumber")
        }
    }

    override fun stopContest(contestNumber: Int) {
        contestService.findByContestNumberAndStatus(ContestStatus.RUNNING, contestNumber)
            .also {
                it.contestStatus = ContestStatus.STOPPED
                contestService.saveContest(it)
                stopOrderProcessing()
                stopMaintainInvestments()
                logger.info("Stopping contest $contestNumber")
            }
    }

    override fun completeContest(contestNumber: Int) {
        contestService.findByContestNumber(contestNumber)
            .takeIf { contest -> contest.contestStatus in listOf(ContestStatus.RUNNING, ContestStatus.STOPPED) }
            ?.also {
                it.contestStatus = ContestStatus.COMPLETED
                contestService.saveContest(it)
                completeContestTasks(it)
                logger.info("Completing contest $contestNumber")
            }
    }

    private fun completeContestTasks(contest: Contest) {
        CoroutineScope(Dispatchers.Default).launch {
            completeOrderProcessing()
            completeMaintainInvestments()
            investmentOrderService.terminateRemainingOrders(contest)

            if (contest.leaderboardUpdateStatus != LeaderboardUpdateStatus.COMPLETED) {
                leaderboardService.updateLeaderboard(contest)
                contest.leaderboardUpdateStatus = LeaderboardUpdateStatus.COMPLETED
                withContext(Dispatchers.IO) {
                    contestService.saveContest(contest)
                }
            }
        }
    }

    override fun startOrderProcessing() {
        assertJobNotAlreadyActive(orderJob)

        if (existsRunningContest()) {
            orderJob = CoroutineScope(Dispatchers.Default).launch {
                logger.info("Starting maintenance of investment orders")
                while (isActive) {
                    orderProcessService.processInvestmentOrders()
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
            investmentJob = CoroutineScope(Dispatchers.Default).launch {
                logger.info("Starting maintenance of investment returns")
                while (isActive) {
                    maintainParticipants()
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


    override fun maintainParticipants() {
        /*
        try {
            investmentService.updateInvestments()
            updateParticipants()
            updateRanking()
        } catch (e: Exception) {
            logger.error("Failed return maintenance : ${e.message}")
        }
         */
    }


    private fun updateParticipants() {
        /*
        participantRepository.findAllByContestStatus(ContestStatus.RUNNING)
            .onEach { participant -> participant.updateValues() }
            .also { participantRepository.saveAll(it) }

         */
    }

    private fun updateRanking() {
        /*
        var rankCounter = 1
        participantRepository.findAllByContestOrderByTotalValueDesc(
            contestService.getContests(listOf(ContestStatus.RUNNING)).first()
        ).onEach { it.rank = rankCounter++ }
            .also { participantRepository.saveAll(it) }

         */
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
        contestService.getContests(listOf(ContestStatus.RUNNING)).isNotEmpty()
}