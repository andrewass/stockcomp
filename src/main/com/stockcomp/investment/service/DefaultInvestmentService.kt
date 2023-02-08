package com.stockcomp.investment.service

import com.stockcomp.contest.dto.RealTimePrice
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.SymbolService
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.service.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultInvestmentService(
    private val investmentRepository: InvestmentRepository,
    private val participantService: ParticipantService,
    private val symbolService: SymbolService
) : InvestmentService {

    private val logger = LoggerFactory.getLogger(DefaultInvestmentService::class.java)

    override fun getInvestmentForSymbol(contestNumber: Int, ident: String, symbol: String): Investment? =
        participantService.getParticipant(contestNumber, ident)!!
            .investments.firstOrNull { it.symbol == symbol }


    override fun getAllInvestmentsForParticipant(ident: String): List<Investment> =
        participantService.getActiveParticipantsByUser(ident)
            .flatMap { investmentRepository.findAllByParticipant(it) }


    override fun updateInvestments() {
        investmentRepository.findAllByContestStatus(ContestStatus.RUNNING)
            .groupBy { it.symbol }
            .forEach { (symbol, investments) ->
                logger.info("Maintaining returns for symbol $symbol")
                val realTimePrice = symbolService.getRealTimePrice(symbol)
                investments.forEach { updateInvestment(it, realTimePrice) }
            }
    }

    private fun updateInvestment(investment: Investment, realTimePrice: RealTimePrice) {
        investment.updateValues(realTimePrice.usdPrice)
        investmentRepository.save(investment)
    }
}