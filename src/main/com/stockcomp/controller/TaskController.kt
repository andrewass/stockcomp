package com.stockcomp.controller

import com.stockcomp.service.contest.DefaultContestService
import com.stockcomp.tasks.ContestTasks
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/task")
class TaskController(
    private val contestService: DefaultContestService,
    private val contestTasks: ContestTasks
) {

    @PostMapping("/start-contest")
    fun startContest(@RequestParam("contestNumber") contestNumber: Int): ResponseEntity<HttpStatus> =
        contestService.startContest(contestNumber)
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/stop-contest")
    fun stopContest(@RequestParam("contestNumber") contestNumber: Int): ResponseEntity<HttpStatus> =
        contestService.stopContest(contestNumber)
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/start-order-process")
    @ApiOperation(value = "Start processing of investment orders")
    fun startOrderProcessing(): ResponseEntity<HttpStatus> =
        contestTasks.startOrderProcessing()
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/stop-order-process")
    @ApiOperation(value = "Stop processing of investment orders")
    fun stopOrderProcessing(): ResponseEntity<HttpStatus> =
        contestTasks.stopOrderProcessing()
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/start-investment-maintenance")
    @ApiOperation("Start maintenance of all participants of a running contest")
    fun startParticipantsMaintenance(): ResponseEntity<HttpStatus> =
        contestTasks.startInvestmentProcessing()
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/stop-investment-maintenance")
    @ApiOperation("Stop maintenance of all participants of a running contest")
    fun stopParticipantsMaintenance(): ResponseEntity<HttpStatus> =
        contestTasks.stopInvestmentProcessing()
            .run { ResponseEntity(HttpStatus.OK) }
}