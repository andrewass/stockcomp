package com.stockcomp.tasks

interface ContestTasks {

    fun startOrderProcessing()

    fun stopOrderProcessing()

    fun startMaintainInvestments()

    fun stopMaintainInvestments()

    fun startContestTasks()

    fun stopContestTasks()

    fun completeContestTasks(contestNumber: Int)
}