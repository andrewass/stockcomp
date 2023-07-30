package com.stockcomp.investment.service

import com.stockcomp.contest.dto.CurrentPriceSymbol
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.SymbolService
import com.stockcomp.investmentorder.service.DefaultInvestmentOrderProcessService
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.repository.InvestmentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultInvestmentProcessService(
    private val investmentRepository: InvestmentRepository,
    private val symbolService: SymbolService
) : InvestmentProcessService {

    private val logger = LoggerFactory.getLogger(DefaultInvestmentOrderProcessService::class.java)

    override fun maintainInvestments() {
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