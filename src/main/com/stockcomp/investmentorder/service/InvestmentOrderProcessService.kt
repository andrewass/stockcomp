package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest

interface InvestmentOrderProcessService {
    fun processInvestmentOrders()

    fun terminateRemainingOrders(contest: Contest)
}