package com.stockcomp.tasks

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.service.investment.MaintainInvestmentService
import com.stockcomp.service.order.MaintainOrderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class DefaultContestTasks(
    private val contestRepository: ContestRepository,
    private val maintainParticipantsService: MaintainInvestmentService,
    private val maintainInvestmentService: MaintainOrderService
) : ContestTasks {

    private val logger = LoggerFactory.getLogger(DefaultContestTasks::class.java)
    private var maintainReturnsEnabled = AtomicBoolean(false)
    private var maintainOrdersEnables = AtomicBoolean(false)

    override fun startOrderProcessing() {
        TODO("Not yet implemented")
    }

    override fun stopOrderProcessing() {
        maintainReturnsEnabled.set(false)
        logger.info("Stopping maintenance of investment orders")
    }

    override fun startInvestmentProcessing() {
        if (!maintainReturnsIsEnabled() && existsRunningContest()) {
            maintainReturnsEnabled.set(true)
            logger.info("Starting maintenance of investment returns")
            CoroutineScope(Default).launch {
                maintainParticipantsService.maintainInvestments()
            }
        }
    }

    override fun stopInvestmentProcessing() {
        maintainReturnsEnabled.set(false)
        logger.info("Stopping maintenance of investment returns")
    }

    override fun terminateRemainingOrders(contest: Contest) {
        TODO("Not yet implemented")
    }


    private fun maintainReturnsIsEnabled(): Boolean =
        maintainReturnsEnabled.get()


    private fun existsRunningContest(): Boolean =
        contestRepository.findAllByContestStatus(ContestStatus.RUNNING).isNotEmpty()

}