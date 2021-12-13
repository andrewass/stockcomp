package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest

interface OrderProcessingService {

    fun startOrderProcessing()

    fun stopOrderProcessing()

    fun terminateRemainingOrders(contest: Contest)
}