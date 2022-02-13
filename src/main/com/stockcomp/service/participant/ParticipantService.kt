package com.stockcomp.service.participant

import com.stockcomp.dto.contest.InvestmentDto

interface ParticipantService {

    fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): InvestmentDto?

    fun getRemainingFunds(username: String, contestNumber: Int): Double

    fun getTotalValue(username: String, contestNumber: Int): Double

    fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<InvestmentDto>
}