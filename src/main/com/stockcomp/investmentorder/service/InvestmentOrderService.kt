package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus

interface InvestmentOrderService {

    fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest)

    fun deleteInvestmentOrder(username: String, orderId: Long): Long

    fun getOrdersByStatus(
        contestNumber: Int, statusList: List<OrderStatus>, ident: String
    ): List<InvestmentOrder>

    fun getSymbolOrdersByStatus(
        contestNumber: Int, symbol: String,
        statusList: List<OrderStatus>, ident: String
    ): List<InvestmentOrder>

    fun terminateRemainingOrders(contest: Contest)
}