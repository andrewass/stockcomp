package com.stockcomp.service.investment

import com.stockcomp.response.InvestmentDto

interface InvestmentService {

    fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): InvestmentDto?

    fun getRemainingFunds(username: String, contestNumber: Int): Double

    fun getTotalValue(username: String, contestNumber: Int): Double

    fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<InvestmentDto>
}