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

    private fun updateInvestmentAndParticipant(investment: Investment, realTimePriceDto: RealTimePriceDto) {
        val oldTotalValueInvestment = investment.totalValue
        val newTotalValueInvestment = (investment.amount * realTimePriceDto.usdPrice)
        investment.apply {
            totalValue = newTotalValueInvestment
            totalProfit = newTotalValueInvestment - (amount * averageUnitCost)
        }
        val participant = investment.participant
        participant.totalValue = participant.remainingFunds + (newTotalValueInvestment - oldTotalValueInvestment)
        investmentRepository.save(investment)
        participantRepository.save(participant)
    }

    private fun updateInvestment(investment: Investment, realTimePrice: RealTimePriceDto){
        val newTotalValueInvestment = (investment.amount * realTimePrice.usdPrice)
        investment.apply {
            totalValue = newTotalValueInvestment
            totalProfit = newTotalValueInvestment - (amount * averageUnitCost)
        }
        investmentRepository.save(investment)
    }

    private fun updateParticipants() {
        participantRepository.findAllByContestStatus(RUNNING)
            .onEach { it.totalValue  }
    }

    private fun updateRanking() {
        var rankCounter = 1
        participantRepository.findAllByContestOrderByTotalValueDesc(contestRepository.findByContestStatus(RUNNING))
            .onEach { it.rank = rankCounter++ }
            .also { participantRepository.saveAll(it) }
    }

}