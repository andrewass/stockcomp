package com.stockcomp.participant.service

import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.entity.Investment


interface InvestmentService {

    fun getInvestmentForSymbol(request: GetInvestmentBySymbolRequest): Investment?

    fun getAllInvestmentsForParticipant(ident: String, contestNumber: Int): List<Investment>

    fun updateInvestments()
}