package com.stockcomp.participant.investment

import com.stockcomp.participant.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InvestmentService(
    private val investmentRepository: InvestmentRepository,
    private val participantService: ParticipantService
) {

    fun getInvestmentForSymbol(contestNumber: Int, email: String, symbol: String): Investment? =
        participantService.getParticipant(contestNumber, email)!!
            .investments.firstOrNull { it.symbol == symbol }

    fun getAllInvestmentsForParticipant(email: String): List<Investment> =
        participantService.getActiveParticipants(email)
            .flatMap { investmentRepository.findAllByParticipant(it) }
}