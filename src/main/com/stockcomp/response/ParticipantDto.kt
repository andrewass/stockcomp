package com.stockcomp.response

data class ParticipantDto(
    val username : String,
    val rank: Int?,
    val totalValue: Double
)
