package com.stockcomp.controller

import com.stockcomp.service.contest.DefaultContestService
import com.stockcomp.service.investment.MaintainParticipantsService
import com.stockcomp.service.order.OrderProcessingService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/task")
class TaskController(
    private val contestService: DefaultContestService,
    private val orderProcessingService: OrderProcessingService,
    private val maintainParticipantsService: MaintainParticipantsService
) {

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

    @PostMapping("/start-participants-maintenance")
    @ApiOperation("Start maintenance of all participants of a running contest")
    fun startParticipantsMaintenance(): ResponseEntity<HttpStatus> {
        maintainParticipantsService.startParticipantsMaintenance()

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/stop-participants-maintenance")
    @ApiOperation("Stop maintenance of all participants of a running contest")
    fun stopParticipantsMaintenance(): ResponseEntity<HttpStatus> {
        maintainParticipantsService.stopParticipantsMaintenance()

        return ResponseEntity(HttpStatus.OK)
    }
}