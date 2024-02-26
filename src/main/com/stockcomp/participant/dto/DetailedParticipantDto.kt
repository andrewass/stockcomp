package com.stockcomp.participant.dto

import com.stockcomp.investment.dto.InvestmentDto

data class DetailedParticipantDto(
    val participant: ParticipantDto,
    val investments: List<InvestmentDto>
)
