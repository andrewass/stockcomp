package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.dto.contest.InvestmentOrderDto
import com.stockcomp.request.InvestmentOrderRequest

interface InvestmentOrderService {

    fun placeBuyOrder(investmentRequest: InvestmentOrderRequest, username: String)

    fun placeSellOrder(investmentRequest: InvestmentOrderRequest, username: String)

    fun deleteActiveInvestmentOrder(username: String, orderId: Long)

    fun getOrdersByStatus(username: String, contestNumber: Int, status: List<OrderStatus>): List<InvestmentOrderDto>

    fun getSymbolOrdersByStatus(username: String, contestNumber: Int, status: List<OrderStatus>, symbol: String):
            List<InvestmentOrderDto>

    fun getAllCompletedOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto>

    fun getAllCompletedOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto>

    fun getAllActiveOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto>

    fun getAllActiveOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto>

    fun terminateRemainingOrders(contest: Contest)
}