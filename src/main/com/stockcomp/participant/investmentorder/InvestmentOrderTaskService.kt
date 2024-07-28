package com.stockcomp.participant.investmentorder

import com.stockcomp.participant.participant.ParticipantService
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class InvestmentOrderTaskService(
    private val symbolService: SymbolServiceExternal,
    private val participantService: ParticipantService,
) {

    @Transactional
    fun processInvestmentOrders(participantId: Long) {
        val participant = participantService.getLockedParticipant(participantId)
        participant.getActiveInvestmentOrders()
            .forEach {
                val currentPrice = symbolService.getCurrentPrice(it.symbol)
                it.processOrder(currentPrice.currentPrice)
                participantService.saveParticipant(participant)
            }
    }
}