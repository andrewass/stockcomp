package com.stockcomp.tasks

import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.service.investment.MaintainInvestmentService
import com.stockcomp.service.order.ProcessOrdersService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultContestTasks(
    private val contestRepository: ContestRepository,
    private val maintainInvestmentService: MaintainInvestmentService,
    private val processOrdersService: ProcessOrdersService
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
        assertJobIsActive(orderJob).cancel()
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
        assertJobIsActive(investmentJob).cancel()
        investmentJob = null
        logger.info("Stopping maintenance of investment returns")
    }

    private fun assertJobNotAlreadyActive(job: Job?) {
        if (job?.isActive == true) {
            throw IllegalStateException("Can not start already running job")
        }
    }

    private fun assertJobIsActive(job: Job?): Job {
        if (job == null || !job.isActive) {
            throw IllegalStateException("Can not stop a non-running job")
        }
        return job
    }

    private fun existsRunningContest(): Boolean =
        contestRepository.findAllByContestStatus(ContestStatus.RUNNING).isNotEmpty()
}