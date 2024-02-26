package com.stockcomp.investmentorder.service

import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus

interface InvestmentOrderService {

    fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest, email: String)

    fun deleteInvestmentOrder(email: String, orderId: Long, contestNumber: Int): Long

    fun getAllOrdersByStatus(statusList: List<OrderStatus>, email: String) : List<InvestmentOrder>

    fun getSymbolOrdersByStatus(
        contestNumber: Int, symbol: String,
        statusList: List<OrderStatus>, email: String
    ): List<InvestmentOrder>
}