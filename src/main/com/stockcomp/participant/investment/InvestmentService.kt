package com.stockcomp.participant.investment

import com.stockcomp.participant.participant.Participant
import com.stockcomp.participant.participant.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InvestmentService(
    private val participantService: ParticipantService
) {

    fun getInvestmentForSymbol(contestId: Long, userId: Long, symbol: String): Investment? =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .investments.firstOrNull { it.symbol == symbol }

    fun getInvestmentsForParticipant(contestId: Long, userId: Long): List<Investment> =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .investments

    fun getInvestmentsDtoForParticipant(participant: Participant): List<InvestmentDto> =
        participant.investments.map { mapToInvestmentDto(it) }

    fun getInvestmentsDtoForParticipant(participant: Participant, symbol: String): List<InvestmentDto> =
        participant.investments.filter { it.symbol == symbol }
            .map { mapToInvestmentDto(it) }
}