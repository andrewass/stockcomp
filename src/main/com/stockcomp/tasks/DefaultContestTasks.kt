package com.stockcomp.tasks

import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.service.investment.MaintainInvestmentService
import com.stockcomp.service.order.ProcessOrdersService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class DefaultContestTasks(
    private val contestRepository: ContestRepository,
    private val maintainParticipantsService: MaintainInvestmentService,
    private val processOrdersService: ProcessOrdersService
) : ContestTasks {

    private val logger = LoggerFactory.getLogger(DefaultContestTasks::class.java)
    private var maintainInvestments = AtomicBoolean(false)
    private var processOrders = AtomicBoolean(false)

    override fun startOrderProcessing() {
        if (!processOrdersIsEnabled() && existsRunningContest()) {
            processOrders.set(true)
            logger.info("Starting processing of investment orders")

            CoroutineScope(Default).launch {
                while (processOrdersIsEnabled()) {
                    processOrdersService.processInvestmentOrders()
                    delay(15000L)
                }
                logger.info("Processing of investment orders stopped")
            }
        }
    }

    override fun stopOrderProcessing() {
        maintainInvestments.set(false)
        logger.info("Stopping processing of investment orders")
    }

    override fun startMaintainInvestments() {
        if (!maintainInvestmentsIsEnabled() && existsRunningContest()) {
            maintainInvestments.set(true)
            logger.info("Starting maintenance of investment returns")

            CoroutineScope(Default).launch {
                while (maintainInvestmentsIsEnabled()) {
                    maintainParticipantsService.maintainInvestments()
                    delay(15000L)
                }
                logger.info("Maintenance of investment returns stopped")
            }
        }
    }

    override fun stopMaintainInvestments() {
        maintainInvestments.set(false)
        logger.info("Stopping maintenance of investment returns")
    }

    private fun maintainInvestmentsIsEnabled(): Boolean = maintainInvestments.get()

    private fun processOrdersIsEnabled(): Boolean = processOrders.get()


    private fun existsRunningContest(): Boolean =
        contestRepository.findAllByContestStatus(ContestStatus.RUNNING).isNotEmpty()

}