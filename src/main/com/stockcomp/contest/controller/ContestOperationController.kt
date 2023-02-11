package com.stockcomp.contest.controller

import com.stockcomp.contest.service.ContestOperationService
import com.stockcomp.exception.handler.CustomExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contest-operations")
class ContestOperationController(
    private val contestOperationService: ContestOperationService
) : CustomExceptionHandler() {

    @PostMapping("/update-leaderboard")
    fun updateLeaderboard() {
        contestOperationService.updateLeaderboard()
    }

    @PostMapping("/maintain-contest-status")
    fun maintainContestStatus() {
        contestOperationService.maintainContestStatus()
    }

    @PostMapping("/maintain-investments")
    fun maintainInvestments() {
        contestOperationService.maintainInvestments()
    }

    @PostMapping("/process-investment-orders")
    fun processInvestmentOrders() {
        contestOperationService.processInvestmentOrders()
    }
}