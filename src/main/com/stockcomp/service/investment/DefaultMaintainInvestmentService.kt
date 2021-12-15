package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.enums.ContestStatus.RUNNING
import com.stockcomp.dto.RealTimePrice
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.InvestmentRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.symbol.SymbolService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultMaintainInvestmentService(
    private val investmentRepository: InvestmentRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository,
    private val symbolService: SymbolService
) : MaintainInvestmentService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainInvestmentService::class.java)

    override fun maintainInvestments() {
        try {
            investmentRepository.findAllInvestmentsByContestStatus(RUNNING)
                .groupBy { it.symbol }
                .forEach { (symbol, investment) ->
                    logger.info("Maintaining returns for symbol $symbol")
                    investment.forEach {
                        updateInvestmentAndParticipant(it, symbolService.getRealTimePrice(symbol))
                    }
                }
            maintainRanking()
        } catch (e: Exception) {
            logger.error("Failed return maintenance : ${e.message}")
        }
    }

    private fun maintainRanking() {
        var rankCounter = 1
        participantRepository.findAllByContestOrderByTotalValueDesc(contestRepository.findByContestStatus(RUNNING))
            .onEach { it.rank = rankCounter++ }
            .also { participantRepository.saveAll(it) }
    }

    private fun updateInvestmentAndParticipant(investment: Investment, realTimePrice: RealTimePrice) {
        val gains = (investment.amount * realTimePrice.usdPrice) - investment.totalValue
        val participant = investment.participant
        participant.totalValue += gains
        investment.apply {
            totalValue += gains
            totalProfit = totalValue - (amount * averageUnitCost)
        }
        investmentRepository.save(investment)
        participantRepository.save(participant)
    }
}