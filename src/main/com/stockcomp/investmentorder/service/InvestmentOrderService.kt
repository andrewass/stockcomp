package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder

interface InvestmentOrderService {

    fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest)

    fun deleteInvestmentOrder(username: String, orderId: Long): Long

    fun getOrdersByStatus(request: GetInvestmentOrderRequest): List<InvestmentOrder>

    fun getSymbolOrdersByStatus(request: GetInvestmentOrderRequest): List<InvestmentOrder>

    fun terminateRemainingOrders(contest: Contest)
}