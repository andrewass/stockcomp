package com.stockcomp.participant.dto

data class ParticipantDto(
    val displayName : String?,
    val rank: Int?,
    val totalValue: Double,
    val totalInvestmentValue: Double,
    val remainingFunds: Double,
    val country: String?,
    val contestNumber: Int
)