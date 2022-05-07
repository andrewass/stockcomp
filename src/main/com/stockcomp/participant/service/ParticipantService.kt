package com.stockcomp.participant.service

import com.stockcomp.participant.entity.Investment

interface ParticipantService {

    fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): Investment?

    fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<Investment>
}