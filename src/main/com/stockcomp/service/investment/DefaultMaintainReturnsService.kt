package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Investment
import com.stockcomp.repository.InvestmentRepository
import com.stockcomp.response.RealTimePrice
import com.stockcomp.service.symbol.SymbolService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultMaintainReturnsService(
    private val investmentRepository: InvestmentRepository,
    private val symbolService: SymbolService
) : MaintainReturnsService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainReturnsService::class.java)
    private var keepMaintainingReturns = false

    init {
        startReturnsMaintenance()
    }

    final override fun startReturnsMaintenance() {
        keepMaintainingReturns = true
        logger.info("Starting maintenance of investment returns")
        GlobalScope.launch {
            maintainReturns()
        }
    }

    override fun stopReturnsMaintenance() {
        keepMaintainingReturns = false
        logger.info("Stopping maintenance of investment returns")
    }

    private suspend fun maintainReturns() {
        while (keepMaintainingReturns) {
            try {
                delay(30000L)
                val investmentMap = investmentRepository.findAll().groupBy { it.symbol }
                investmentMap.forEach { (symbol, investment) ->
                    run {
                        logger.info("Maintaining returns for symbol $symbol")
                        val realTimePrice = symbolService.getRealTimePrice(symbol)
                        investment.forEach { updateInvestment(it, realTimePrice) }
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed return maintenance : ${e.message}")
            }
        }
    }

    private fun updateInvestment(investment: Investment, realTimePrice: RealTimePrice) {
        val currentValue = investment.amount * realTimePrice.usdPrice
        val currentExpenses = investment.amount * investment.averageUnitCost
        investment.totalProfit = currentValue - currentExpenses
        investmentRepository.save(investment)
    }
}