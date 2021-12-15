package com.stockcomp.tasks

import com.stockcomp.domain.contest.Contest

interface ContestTasks {

    fun startOrderProcessing()

    fun stopOrderProcessing()

    fun startInvestmentProcessing()

    fun stopInvestmentProcessing()

    fun terminateRemainingOrders(contest: Contest)
}