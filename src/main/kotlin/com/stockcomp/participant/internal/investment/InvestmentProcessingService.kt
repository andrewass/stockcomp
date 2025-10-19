package com.stockcomp.participant.internal.investment

import com.stockcomp.participant.internal.ParticipantRepository
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InvestmentProcessingService(
    private val participantRepository: ParticipantRepository,
    private val symbolService: SymbolServiceExternal,
) {

    fun maintainInvestments(participantId: Long) {
        val participant = participantRepository.findByIdLocked(participantId)
        participant.investments
            .onEach {
                val price = symbolService.getCurrentPrice(it.symbol)
                it.maintainInvestment(price.currentPrice)
            }
        participant.updateInvestmentValues()
        participantRepository.save(participant)
    }

    fun getInvestmentForSymbol(contestId: Long, userId: Long, symbol: String): Investment? =
        participantRepository.findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.investments?.firstOrNull { it.symbol == symbol }

    fun getInvestmentsForParticipant(contestId: Long, userId: Long): List<Investment> =
        participantRepository.findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.investments ?: emptyList()
}