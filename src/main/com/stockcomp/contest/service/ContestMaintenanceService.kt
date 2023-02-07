package com.stockcomp.contest.service

interface ContestMaintenanceService {

    fun startOrderProcessing()

    fun stopOrderProcessing()

    fun startMaintainInvestments()

    fun stopMaintainInvestments()

    fun maintainParticipants()

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)
}