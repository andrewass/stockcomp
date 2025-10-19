package com.stockcomp.participant.internal.investment

data class InvestmentDto(
    val symbol: String,
    val amount: Int,
    val averageUnitCost: Double,
    val totalProfit: Double,
    val totalValue: Double
)

fun mapToInvestmentDto(source: Investment) =
    InvestmentDto(
        amount = source.amount,
        averageUnitCost = source.averageUnitCost,
        symbol = source.symbol,
        totalProfit = source.totalProfit,
        totalValue = source.totalValue
    )
