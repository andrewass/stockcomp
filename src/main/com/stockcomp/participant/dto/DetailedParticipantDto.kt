package com.stockcomp.participant.dto

data class DetailedParticipantDto(
    val participant: ParticipantDto,
    val investments: List<InvestmentDto>
)
