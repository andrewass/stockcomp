package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.enums.ContestStatus.RUNNING
import com.stockcomp.dto.RealTimePrice
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.InvestmentRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.symbol.SymbolService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultMaintainParticipantsService(
    private val investmentRepository: InvestmentRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository,
    private val symbolService: SymbolService
) : MaintainParticipantsService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainParticipantsService::class.java)
    private var keepMaintainingReturns = false

    override fun startParticipantsMaintenance() {
        keepMaintainingReturns = true
        logger.info("Starting maintenance of investment returns")
        CoroutineScope(Default).launch {
            maintainParticipants()
        }
    }

    override fun stopParticipantsMaintenance() {
        keepMaintainingReturns = false
        logger.info("Stopping maintenance of investment returns")
    }

    private suspend fun maintainParticipants() {
        while (keepMaintainingReturns) {
            try {
                delay(30000L)
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
        logger.info("Maintenance of investment returns is now stopped")
    }

    private fun maintainRanking() {
        val runningContest = contestRepository.findByContestStatus(RUNNING)
        var rank = 1
        val participants = participantRepository.findAllByContestOrderByTotalValueDesc(runningContest)
        participants.forEach { it.rank = rank++ }
        participantRepository.saveAll(participants)
    }

    private fun updateInvestmentAndParticipant(investment: Investment, realTimePrice: RealTimePrice) {
        val gains = (investment.amount * realTimePrice.usdPrice) - investment.totalValue
        val participant = investment.participant
        participant.totalValue += gains
        investment.apply {
            totalValue += gains
            totalProfit = this.totalValue - (investment.amount * investment.averageUnitCost)
        }
        investmentRepository.save(investment)
        participantRepository.save(participant)
    }
}