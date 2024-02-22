package com.stockcomp.investment.task

import com.stockcomp.contest.dto.CurrentPriceSymbol
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.SymbolService
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.repository.InvestmentRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvestmentTasks(
    private val investmentRepository: InvestmentRepository,
    private val symbolService: SymbolService,
) {
    private val logger = LoggerFactory.getLogger(InvestmentTasks::class.java)

    @Scheduled(fixedRate = 30000)
    fun runMaintainInvestments() {
        investmentRepository.findAllByContestStatus(ContestStatus.RUNNING)
            .groupBy { it.symbol }
            .forEach { (symbol, investments) ->
                logger.info("Maintaining returns for symbol $symbol")
                val realTimePrice = symbolService.getCurrentPrice(symbol)
                investments.forEach { updateInvestment(it, realTimePrice) }
            }
    }

    private fun updateInvestment(investment: Investment, realTimePrice: CurrentPriceSymbol) {
        investment.updateValues(realTimePrice.currentPrice)
        investmentRepository.save(investment)
    }
}