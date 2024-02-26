package com.stockcomp.investment.dto

data class GetInvestmentBySymbolRequest(
    val symbol: String,
    val contestNumber: Int
)
