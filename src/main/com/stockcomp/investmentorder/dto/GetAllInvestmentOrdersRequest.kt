package com.stockcomp.investmentorder.dto

import com.stockcomp.investmentorder.entity.OrderStatus

data class GetAllInvestmentOrdersRequest(
    val statusList: List<OrderStatus>
)
