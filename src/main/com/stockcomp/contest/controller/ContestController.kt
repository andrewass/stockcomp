package com.stockcomp.contest.controller

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.dto.ContestPageDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.service.ContestService
import com.stockcomp.exception.handler.CustomExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/contest")
class ContestController(
    private val contestService: ContestService
) : CustomExceptionHandler() {

    @GetMapping("/sorted")
    fun getAllContestsSortedByContestNumber(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<ContestPageDto> =
        contestService.getAllContestsSorted(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToContestPageDto(it)) }

    @GetMapping("/active")
    fun getActiveContests(): ResponseEntity<List<ContestDto>> =
        contestService.getActiveContests()
            .map { mapToContestDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping
    fun getContest(@RequestParam contestNumber: Int): ResponseEntity<ContestDto> =
        contestService.findByContestNumber(contestNumber)
            .let { ResponseEntity.ok(mapToContestDto(it)) }

    @PostMapping("/create")
    fun createContest(@RequestBody contestRequest: CreateContestRequest): ResponseEntity<HttpStatus> =
        contestService.createContest(contestRequest)
            .let { ResponseEntity(HttpStatus.OK) }

    @PatchMapping("/update")
    fun updateContest(@RequestBody contestRequest: UpdateContestRequest): ResponseEntity<HttpStatus> =
        contestService.updateContest(contestRequest)
            .let { ResponseEntity(HttpStatus.OK) }

    @DeleteMapping("/delete")
    fun deleteContest(@RequestParam contestNumber: Int): ResponseEntity<HttpStatus> =
        contestService.deleteContest(contestNumber)
            .let { ResponseEntity(HttpStatus.OK) }
}