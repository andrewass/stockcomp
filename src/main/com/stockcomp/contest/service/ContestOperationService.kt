package com.stockcomp.contest.service

interface ContestOperationService {
    fun updateLeaderboard()

    fun maintainContestStatus()

    fun maintainInvestments()

    fun processInvestmentOrders()
}