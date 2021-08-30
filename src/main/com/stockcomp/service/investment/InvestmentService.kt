package com.stockcomp.service.investment

import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto

interface InvestmentService {
    fun placeBuyOrder(request : InvestmentTransactionRequest, username : String)

    fun placeSellOrder(request: InvestmentTransactionRequest, username: String)

    fun getInvestmentForSymbol(username: String, contestNumber : Int, symbol: String) : InvestmentDto?

    fun getRemainingFunds(username: String, contestNumber: Int) : Double

    fun getTotalValue(username: String, contestNumber: Int): Double
}