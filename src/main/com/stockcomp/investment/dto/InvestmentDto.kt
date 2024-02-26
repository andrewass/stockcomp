package com.stockcomp.investment.dto

data class InvestmentDto(
    val symbol: String,
    val amount: Int,
    val averageUnitCost: Double,
    val totalProfit: Double,
    val totalValue: Double,
    val contestNumber: Int
)
