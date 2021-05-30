package com.stockcomp.service.order

import com.stockcomp.response.InvestmentOrderDto

interface InvestmentOrderService{

    fun getAllCompletedOrdersForParticipant(): List<InvestmentOrderDto>

    fun getAllCompletedOrdersForSymbolForParticipant() : List<InvestmentOrderDto>

    fun getAllActiveOrders() : List<InvestmentOrderDto>

    fun getAllActiveOrdersForSymbol() : List<InvestmentOrderDto>
}