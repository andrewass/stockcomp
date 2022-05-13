package com.stockcomp.investmentorder.dto

import com.stockcomp.investmentorder.entity.OrderStatus

data class GetInvestmentOrderRequest(
    val contestNumber : Int,
    val statusList: List<OrderStatus>
)
