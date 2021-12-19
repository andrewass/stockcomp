package com.stockcomp.tasks

interface ContestTasks {

    fun startOrderProcessing()

    fun stopOrderProcessing()

    fun startMaintainInvestments()

    fun startContestTasks()

    fun stopContestTasks()

    fun stopMaintainInvestments()
}