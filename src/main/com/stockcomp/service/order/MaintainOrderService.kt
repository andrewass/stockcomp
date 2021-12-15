package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest

interface MaintainOrderService {

    suspend fun processInvestmentOrders()

    fun terminateRemainingOrders(contest: Contest)
}