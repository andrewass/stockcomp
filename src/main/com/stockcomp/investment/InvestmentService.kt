package com.stockcomp.investment

import com.stockcomp.investment.entity.Investment
import com.stockcomp.participant.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InvestmentService(
    private val investmentRepository: InvestmentRepository,
    private val participantService: ParticipantService
) {

    fun getInvestmentForSymbol(contestNumber: Int, ident: String, symbol: String): Investment? =
        participantService.getParticipant(contestNumber, ident)!!
            .investments.firstOrNull { it.symbol == symbol }

    fun getAllInvestmentsForParticipant(ident: String): List<Investment> =
        participantService.getActiveParticipantsByUser(ident)
            .flatMap { investmentRepository.findAllByParticipant(it) }
}