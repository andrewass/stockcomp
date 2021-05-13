package com.stockcomp.service.util

import com.stockcomp.entity.contest.Investment
import com.stockcomp.response.InvestmentDto

fun mapToInvestmentDto(investment: Investment?, symbol : String) : InvestmentDto {
    return InvestmentDto(
        symbol = symbol,
        amount = investment?.amount ?: 0
    )
}