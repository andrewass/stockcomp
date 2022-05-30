package com.stockcomp.participant.service

import com.stockcomp.contest.dto.RealTimePrice
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.contest.service.SymbolService
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.repository.ParticipantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultMaintainParticipantService(
    private val investmentRepository: InvestmentRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository,
    private val symbolService: SymbolService
) : MaintainParticipantService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainParticipantService::class.java)

    override fun maintainParticipants() {
        try {
            investmentRepository.findAllByContestStatus(ContestStatus.RUNNING)
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

    private fun updateInvestment(investment: Investment, realTimePrice: RealTimePrice) {
        investment.updateValues(realTimePrice.usdPrice)
        investmentRepository.save(investment)
    }

    private fun updateParticipants() {
        participantRepository.findAllByContestStatus(ContestStatus.RUNNING)
            .onEach { participant -> participant.updateValues() }
            .also { participantRepository.saveAll(it) }
    }

    private fun updateRanking() {
        var rankCounter = 1
        participantRepository.findAllByContestOrderByTotalValueDesc(contestRepository.findByContestStatus(ContestStatus.RUNNING))
            .onEach { it.rank = rankCounter++ }
            .also { participantRepository.saveAll(it) }
    }
}