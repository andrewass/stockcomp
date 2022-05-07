package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.investmentorder.dto.InvestmentOrderRequest

interface InvestmentOrderService {

    fun placeInvestmentOrder(investmentRequest: InvestmentOrderRequest, username: String): Long

    fun deleteInvestmentOrder(username: String, orderId: Long): Long

    fun getOrdersByStatus(username: String, contestNumber: Int, status: List<OrderStatus>): List<InvestmentOrder>

    fun getSymbolOrdersByStatus(username: String, contestNumber: Int, status: List<OrderStatus>, symbol: String):
            List<InvestmentOrder>

    fun terminateRemainingOrders(contest: Contest)
}