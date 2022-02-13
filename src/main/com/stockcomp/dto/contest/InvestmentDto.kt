package com.stockcomp.dto.contest

data class InvestmentDto(
    val id: Long,
    val symbol: String,
    val amount: Int,
    val averageUnitCost: Double,
    val totalValue: Double,
    val totalProfit: Double
)