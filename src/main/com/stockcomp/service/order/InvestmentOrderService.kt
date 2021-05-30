package com.stockcomp.service.order

import com.stockcomp.response.InvestmentOrderDto

interface InvestmentOrderService {

    fun getAllCompletedOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto>

    fun getAllCompletedOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto>

    fun getAllActiveOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto>

    fun getAllActiveOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto>

    fun deleteActiveInvestmentOrder(username: String, orderId: Long)
}