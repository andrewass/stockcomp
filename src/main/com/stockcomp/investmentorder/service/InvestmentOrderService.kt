package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.InvestmentOrderDto
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest

interface InvestmentOrderService {

    fun placeInvestmentOrder(investmentRequest: PlaceInvestmentOrderRequest, username: String): Long

    fun deleteInvestmentOrder(username: String, orderId: Long): Long

    fun getOrdersByStatus(username: String, request: GetInvestmentOrderRequest): List<InvestmentOrderDto>

    fun getSymbolOrdersByStatus(username: String, contestNumber: Int, status: List<OrderStatus>, symbol: String):
            List<InvestmentOrder>

    fun terminateRemainingOrders(contest: Contest)
}