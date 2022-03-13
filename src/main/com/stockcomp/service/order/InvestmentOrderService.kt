package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.request.InvestmentOrderRequest

interface InvestmentOrderService {

    fun placeBuyOrder(investmentRequest: InvestmentOrderRequest, username: String)

    fun placeSellOrder(investmentRequest: InvestmentOrderRequest, username: String)

    fun deleteActiveInvestmentOrder(username: String, orderId: Long)

    fun getOrdersByStatus(username: String, contestNumber: Int, status: List<OrderStatus>): List<InvestmentOrder>

    fun getSymbolOrdersByStatus(username: String, contestNumber: Int, status: List<OrderStatus>, symbol: String):
            List<InvestmentOrder>

    fun terminateRemainingOrders(contest: Contest)
}