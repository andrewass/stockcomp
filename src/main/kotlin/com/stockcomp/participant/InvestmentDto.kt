package com.stockcomp.participant

import java.math.BigDecimal

data class InvestmentDto(
    val symbol: String,
    val amount: Int,
    val averageUnitCost: BigDecimal,
    val totalProfit: BigDecimal,
    val totalValue: BigDecimal,
)
