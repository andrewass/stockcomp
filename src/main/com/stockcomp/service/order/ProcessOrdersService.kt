package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest

interface ProcessOrdersService {

    suspend fun processInvestmentOrders()

    fun terminateRemainingOrders(contest: Contest)
}