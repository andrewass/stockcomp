package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder

interface InvestmentOrderService {

    fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest, username: String)

    fun deleteInvestmentOrder(username: String, orderId: Long): Long

    fun getOrdersByStatus(username: String, request: GetInvestmentOrderRequest): List<InvestmentOrder>

    fun getSymbolOrdersByStatus(username: String, request: GetInvestmentOrderRequest):
            List<InvestmentOrder>

    fun terminateRemainingOrders(contest: Contest)
}