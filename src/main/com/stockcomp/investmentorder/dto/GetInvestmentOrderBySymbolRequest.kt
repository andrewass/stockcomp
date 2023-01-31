package com.stockcomp.investmentorder.dto

import com.stockcomp.investmentorder.entity.OrderStatus

data class GetInvestmentOrderBySymbolRequest(
    val contestNumber : Int,
    val statusList: List<OrderStatus>,
    val symbol: String? = null
)
