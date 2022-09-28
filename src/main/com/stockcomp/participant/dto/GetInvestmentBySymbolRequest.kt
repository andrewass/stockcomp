package com.stockcomp.participant.dto

data class GetInvestmentBySymbolRequest(
    val ident: String,
    val symbol: String,
    val contestNumber: Int
)
