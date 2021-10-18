package com.stockcomp.service.order

import com.stockcomp.request.InvestmentOrderRequest
import com.stockcomp.dto.InvestmentOrderDto

interface InvestmentOrderService {

    fun placeBuyOrder(investmentRequest: InvestmentOrderRequest, username: String)

    fun placeSellOrder(investmentRequest: InvestmentOrderRequest, username: String)

    fun deleteActiveInvestmentOrder(username: String, orderId: Long)

    fun getAllCompletedOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto>

    fun getAllCompletedOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto>

    fun getAllActiveOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto>

    fun getAllActiveOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto>
}