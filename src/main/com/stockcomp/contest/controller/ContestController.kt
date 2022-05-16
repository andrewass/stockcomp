package com.stockcomp.contest.controller

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.producer.common.CustomExceptionHandler
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
class ContestController(
    private val contestService: ContestService,
    private val defaultJwtService: JwtService
) : CustomExceptionHandler() {

    @PostMapping("/get-by-status")
    fun getContestsByStatus(@RequestBody statusList: List<ContestStatus>): ResponseEntity<List<ContestDto>> =
        ResponseEntity.ok(contestService.getContests(statusList))


    @GetMapping("/get-by-number")
    fun getContest(@RequestParam contestNumber: Int): ResponseEntity<ContestDto> =
        ResponseEntity.ok(contestService.getContest(contestNumber))


    @PostMapping("/create")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<ContestDto> =
        ResponseEntity.ok(contestService.createContest(request))


    @PutMapping("/update")
    fun updateContest(@RequestBody request: UpdateContestRequest): ResponseEntity<ContestDto> =
        ResponseEntity.ok(contestService.updateContest(request))


    @DeleteMapping("/delete")
    fun deleteContest(@RequestParam contestNumber: Int): ResponseEntity<HttpStatus> =
        contestService.deleteContest(contestNumber)
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/sign-up")
    fun signUp(@RequestParam contestNumber: Int, servletRequest: HttpServletRequest): ResponseEntity<HttpStatus> {
        contestService.signUp(extractUsernameFromRequest(servletRequest), contestNumber)
        return ResponseEntity(HttpStatus.OK)
    }

    private fun extractUsernameFromRequest(servletRequest: HttpServletRequest): String =
        getAccessTokenFromCookie(servletRequest)
            .let { defaultJwtService.extractUsername(it!!) }
}