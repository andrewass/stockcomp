package com.stockcomp.participant.dto

data class GetInvestmentBySymbolRequest(
    val symbol: String,
    val contestNumber: Int
)
