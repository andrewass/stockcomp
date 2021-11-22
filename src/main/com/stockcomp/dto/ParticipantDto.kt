package com.stockcomp.dto

import java.time.LocalDateTime

data class ParticipantDto(
    val username : String,
    val rank: Int?,
    val totalValue: Double,
    val country: String?,
    val startTime: LocalDateTime,
    val contestNumber: Int
)
