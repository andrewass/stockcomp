package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Investment
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.InvestmentRepository
import com.stockcomp.response.RealTimePrice
import com.stockcomp.service.StockService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultMaintainReturnsService(
    private val investmentRepository: InvestmentRepository,
    private val stockService: StockService,
    contestRepository: ContestRepository
) : MaintainReturnsService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainReturnsService::class.java)
    private var launchedJob: Job? = null

    init {
        if (contestRepository.findAllByInRunningModeIsTrue().isNotEmpty()) {
            startReturnsMaintenance()
        }
    }

    final override fun startReturnsMaintenance() {
        if (launchedJob?.isActive == true) {
            logger.warn("Unable to start returns maintenance process. Previous process still running")
            return
        }
        launchedJob = GlobalScope.launch {
            maintainReturns()
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
                val realTimePrice = stockService.getRealTimePrice(symbol)
                investment.forEach { updateInvestment(it, realTimePrice) }
            }
        }
    }

    private fun updateInvestment(investment: Investment, realTimePrice: RealTimePrice) {
        val averagePrice = investment.sumPaid / investment.totalAmountBought
        val averageExpenses = averagePrice * investment.amount
        investment.investmentReturns = realTimePrice.currentPrice * investment.amount - averageExpenses
        investmentRepository.save(investment)
    }
}