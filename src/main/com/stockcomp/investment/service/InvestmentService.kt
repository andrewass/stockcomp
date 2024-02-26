package com.stockcomp.investment.service

import com.stockcomp.investment.entity.Investment

interface InvestmentService {
    fun getInvestmentForSymbol(contestNumber: Int, ident: String, symbol: String): Investment?

    fun getAllInvestmentsForParticipant(ident: String): List<Investment>
}