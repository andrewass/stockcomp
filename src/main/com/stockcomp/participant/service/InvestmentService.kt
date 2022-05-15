package com.stockcomp.participant.service

import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.dto.InvestmentDto


interface InvestmentService {

    fun getInvestmentForSymbol(username: String, request: GetInvestmentBySymbolRequest): InvestmentDto?

    fun getAllInvestmentsForParticipant(username: String, contestNumber: Int): List<InvestmentDto>

}