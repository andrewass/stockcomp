package com.stockcomp.investmentorder.dto

import com.stockcomp.investmentorder.entity.OrderStatus

data class GetAllInvestmentOrders(
    val statusList: List<OrderStatus>
)
