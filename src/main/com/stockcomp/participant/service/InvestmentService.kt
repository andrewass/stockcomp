package com.stockcomp.participant.service

import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.entity.Investment


interface InvestmentService {

    fun getInvestmentForSymbol(username: String, request: GetInvestmentBySymbolRequest): Investment?

    fun getAllInvestmentsForParticipant(username: String, contestNumber: Int): List<Investment>

    fun maintainInvestments()
}