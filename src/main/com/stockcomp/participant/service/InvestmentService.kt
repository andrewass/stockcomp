package com.stockcomp.participant.service

import com.stockcomp.participant.entity.Investment

interface InvestmentService {
    fun getInvestmentForSymbol(contestNumber: Int, ident: String, symbol: String): Investment?

    fun getAllInvestmentsForParticipant(ident: String): List<Investment>

    fun updateInvestments()
}