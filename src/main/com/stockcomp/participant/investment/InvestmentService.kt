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

    fun getInvestmentForSymbol(contestId: Long, userId: Long, symbol: String): Investment? =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .investments.firstOrNull { it.symbol == symbol }

    fun getInvestmentsForParticipant(contestId: Long, userId: Long): List<Investment> =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .investments
}