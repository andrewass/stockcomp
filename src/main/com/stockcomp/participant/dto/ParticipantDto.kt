package com.stockcomp.participant.dto

import java.time.LocalDateTime

data class ParticipantDto(
    val username : String,
    val rank: Int?,
    val totalValue: Double,
    val totalInvestmentValue: Double,
    val remainingFunds: Double,
    val country: String?,
    val startTime: LocalDateTime,
    val contestNumber: Int
)