package com.stockcomp.investment.dto

import com.stockcomp.investment.entity.Investment


fun mapToInvestmentDto(source: Investment) =
    InvestmentDto(
        amount = source.amount,
        averageUnitCost = source.averageUnitCost,
        symbol = source.symbol,
        totalProfit = source.totalProfit,
        totalValue = source.totalValue,
        contestNumber = source.participant.contest.contestNumber
    )