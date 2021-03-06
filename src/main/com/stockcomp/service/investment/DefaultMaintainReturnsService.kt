package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Investment
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.InvestmentRepository
import com.stockcomp.response.RealTimePrice
import com.stockcomp.service.SymbolService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DefaultMaintainReturnsService(
    private val investmentRepository: InvestmentRepository,
    private val symbolService: SymbolService,
    private val contestRepository: ContestRepository
) : MaintainReturnsService {

    @Value("\${auto.start.tasks}")
    private val autoStartTasks: Boolean = false

    private val logger = LoggerFactory.getLogger(DefaultMaintainReturnsService::class.java)
    private var launchedJob: Job? = null

    init {
        if (shouldLaunchTask()) {
            startReturnsMaintenance()
        }
    }

    final override fun startReturnsMaintenance() {
        if (launchedJob?.isActive == true) {
            logger.warn("Unable to start returns maintenance process. Previous process still running")
            return
        }
        launchedJob = GlobalScope.launch {
            while (true) {
                maintainReturns()
                delay(30000L)
            }
        }
    }

    override fun stopReturnsMaintenance() {
        launchedJob?.cancel()
        logger.info("Stopping returns maintenance")
    }

    private fun maintainReturns() {
        val investmentMap = investmentRepository.findAll().groupBy { it.symbol }
        investmentMap.forEach { (symbol, investment) ->
            run {
                val realTimePrice = symbolService.getRealTimePrice(symbol)
                investment.forEach { updateInvestment(it, realTimePrice) }
            }
        }
    }

    private fun updateInvestment(investment: Investment, realTimePrice: RealTimePrice) {
        val averagePrice = investment.sumPaid / investment.totalAmountBought
        val averageExpenses = averagePrice * investment.amount
        investment.apply {
            totalValue = amount * realTimePrice.currentPrice
            investmentReturns = totalValue - averageExpenses
        }
        investmentRepository.save(investment)
    }

    private fun shouldLaunchTask() : Boolean =
        contestRepository.findAllByInRunningModeIsTrue().isNotEmpty() && autoStartTasks

}