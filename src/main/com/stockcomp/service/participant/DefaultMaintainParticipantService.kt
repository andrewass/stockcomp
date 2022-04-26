package com.stockcomp.service.participant

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.enums.ContestStatus.RUNNING
import com.stockcomp.dto.stock.RealTimePriceDto
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.InvestmentRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.symbol.SymbolService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultMaintainParticipantService(
    private val investmentRepository: InvestmentRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository,
    private val symbolService: SymbolService
) : MaintainParticipantService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainParticipantService::class.java)

    override fun maintainParticipants() {
        try {
            investmentRepository.findAllByContestStatus(RUNNING)
                .groupBy { it.symbol }
                .forEach { (symbol, investments) ->
                    logger.info("Maintaining returns for symbol $symbol")
                    val realTimePrice = symbolService.getRealTimePrice(symbol)
                    investments.forEach { updateInvestment(it, realTimePrice) }
                }
            updateParticipants()
            updateRanking()
        } catch (e: Exception) {
            logger.error("Failed return maintenance : ${e.message}")
        }
    }

    private fun updateInvestment(investment: Investment, realTimePrice: RealTimePriceDto) {
        val newTotalValueInvestment = (investment.amount * realTimePrice.usdPrice)
        investment.apply {
            totalValue = newTotalValueInvestment
            totalProfit = newTotalValueInvestment - (amount * averageUnitCost)
        }
        investmentRepository.save(investment)
    }

    private fun updateParticipants() {
        participantRepository.findAllByContestStatus(RUNNING)
            .onEach { participant ->
                val totalInvestmentsValue = participant.investments.sumOf { it.totalValue }
                participant.totalInvestmentValue = totalInvestmentsValue
                participant.totalValue = participant.remainingFunds + totalInvestmentsValue
            }
            .also { participantRepository.saveAll(it) }
    }

    private fun updateRanking() {
        var rankCounter = 1
        participantRepository.findAllByContestOrderByTotalValueDesc(contestRepository.findByContestStatus(RUNNING))
            .onEach { it.rank = rankCounter++ }
            .also { participantRepository.saveAll(it) }
    }

}