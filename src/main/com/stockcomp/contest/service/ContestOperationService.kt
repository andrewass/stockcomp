package com.stockcomp.contest.service

interface ContestOperationService {
    fun maintainContestStatus()

    fun maintainInvestments()

    fun processInvestmentOrders()
}