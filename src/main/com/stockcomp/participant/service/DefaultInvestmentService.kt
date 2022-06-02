package com.stockcomp.participant.service

import com.stockcomp.contest.dto.RealTimePrice
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.SymbolService
import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.repository.InvestmentRepository
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


    override fun getInvestmentForSymbol(username: String, request: GetInvestmentBySymbolRequest): Investment? =
        participantService.getParticipant(request.contestNumber, username)
            .investments.firstOrNull { it.symbol == request.symbol }


    override fun getAllInvestmentsForParticipant(username: String, contestNumber: Int): List<Investment> =
        participantService.getParticipant(contestNumber, username)
            .let { investmentRepository.findAllByParticipant(it) }


    override fun maintainInvestments() {
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