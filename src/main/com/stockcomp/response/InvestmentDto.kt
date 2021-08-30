package com.stockcomp.response

data class InvestmentDto(
    val name: String,
    val symbol: String,
    val amount: Int,
    val averageUnitCost: Double,
    val totalValue: Double,
    val totalProfit: Double
)