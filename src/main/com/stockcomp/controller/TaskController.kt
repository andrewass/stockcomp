package com.stockcomp.controller

import com.stockcomp.domain.contest.Contest
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.service.ContestService
import com.stockcomp.service.investment.MaintainReturnsService
import com.stockcomp.service.order.OrderProcessingService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/task")
class TaskController(
    private val contestService: ContestService,
    private val orderProcessingService: OrderProcessingService,
    private val maintainReturnsService: MaintainReturnsService
) {

    @PostMapping("/create-contest")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<Contest> {
        val contest = contestService.createContest(request)

        return ResponseEntity.ok(contest)
    }

    @PostMapping("/start-contest")
    fun startContest(@RequestParam("contestNumber") contestNumber: Int): ResponseEntity<HttpStatus> {
        contestService.startContest(contestNumber)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/stop-contest")
    fun stopContest(@RequestParam("contestNumber") contestNumber: Int): ResponseEntity<HttpStatus> {
        contestService.stopContest(contestNumber)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/start-order-process")
    @ApiOperation(value = "Start processing of investment orders")
    fun startOrderProcessing(): ResponseEntity<HttpStatus> {
        orderProcessingService.startOrderProcessing()

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/stop-order-process")
    @ApiOperation(value = "Stop processing of investment orders")
    fun stopOrderProcessing(): ResponseEntity<HttpStatus> {
        orderProcessingService.stopOrderProcessing()

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/start-investment-returns-processing")
    @ApiOperation("Start processing of investment returns")
    fun startInvestmentReturnsProcessing() : ResponseEntity<HttpStatus> {
        maintainReturnsService.startReturnsMaintenance()

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/stop-investment-returns-processing")
    @ApiOperation("Stop processing of investment returns")
    fun stopInvestmentReturnsProcessing() : ResponseEntity<HttpStatus> {
        maintainReturnsService.stopReturnsMaintenance()

        return ResponseEntity(HttpStatus.OK)
    }
}