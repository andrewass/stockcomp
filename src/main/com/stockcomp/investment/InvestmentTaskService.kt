package com.stockcomp.investment

import com.stockcomp.contest.service.SymbolService
import com.stockcomp.participant.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InvestmentTaskService(
    private val symbolService: SymbolService,
    private val participantService: ParticipantService,
) {
    @Transactional
    fun maintainInvestments(participantId: Long) {
        val participant = participantService.getLockedParticipant(participantId)
        participant.investments
            .onEach {
                val price = symbolService.getCurrentPrice(it.symbol)
                it.updateValues(price.currentPrice)
            }
        participant.updateInvestmentValues()
        participantService.saveParticipant(participant)
    }
}