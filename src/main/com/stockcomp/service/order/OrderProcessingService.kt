package com.stockcomp.service.order

import com.stockcomp.entity.contest.InvestmentOrder

interface OrderProcessingService {
    fun processOrder(investmentOrder: InvestmentOrder, currentPrice: Double)
}