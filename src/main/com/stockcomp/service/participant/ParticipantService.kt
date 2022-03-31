package com.stockcomp.service.participant

import com.stockcomp.domain.contest.Investment

interface ParticipantService {

    fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): Investment?

    fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<Investment>
}