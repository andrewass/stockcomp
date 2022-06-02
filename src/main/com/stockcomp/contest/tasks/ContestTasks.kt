package com.stockcomp.contest.tasks

interface ContestTasks {

    fun startOrderProcessing()

    fun stopOrderProcessing()

    fun startMaintainInvestments()

    fun stopMaintainInvestments()

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)
}