package com.stockcomp.participant

import com.stockcomp.participant.internal.investment.Investment
import java.math.BigDecimal

data class InvestmentDto(
    val symbol: String,
    val amount: Int,
    val averageUnitCost: BigDecimal,
    val totalProfit: BigDecimal,
    val totalValue: BigDecimal,
)

fun mapToInvestmentDto(source: Investment) =
    InvestmentDto(
        amount = source.amount,
        averageUnitCost = source.averageUnitCost,
        symbol = source.symbol,
        totalProfit = source.totalProfit,
        totalValue = source.totalValue,
    )
