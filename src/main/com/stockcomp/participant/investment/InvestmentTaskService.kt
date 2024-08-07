package com.stockcomp.participant.investment

import com.stockcomp.participant.participant.ParticipantService
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InvestmentTaskService(
    private val symbolService: SymbolServiceExternal,
    private val participantService: ParticipantService,
) {
    @Transactional
    fun maintainInvestments(participantId: Long) {
        val participant = participantService.getLockedParticipant(participantId)
        participant.investments
            .onEach {
                val price = symbolService.getCurrentPrice(it.symbol)
                it.maintainInvestment(price.currentPrice)
            }
        participant.updateInvestmentValues()
        participantService.saveParticipant(participant)
    }
}