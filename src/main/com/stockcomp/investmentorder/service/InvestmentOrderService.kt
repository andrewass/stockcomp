package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.InvestmentOrderDto
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest

interface InvestmentOrderService {

    fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest, username: String)

    fun deleteInvestmentOrder(username: String, orderId: Long): Long

    fun getOrdersByStatus(username: String, request: GetInvestmentOrderRequest): List<InvestmentOrderDto>

    fun getSymbolOrdersByStatus(username: String, request: GetInvestmentOrderRequest):
            List<InvestmentOrderDto>

    fun terminateRemainingOrders(contest: Contest)
}