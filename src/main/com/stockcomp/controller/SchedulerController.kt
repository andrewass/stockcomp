package com.stockcomp.controller

import com.stockcomp.domain.contest.Contest
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.service.ContestService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/scheduler")
class SchedulerController(private val contestService: ContestService) {

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
}