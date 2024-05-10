package com.stockcomp.contest.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.exception.handler.CustomExceptionHandler
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/contests")
class ContestController(
    private val contestService: ContestService,
    private val tokenService: TokenService,
) : CustomExceptionHandler() {

    @PostMapping("/sign-up/{contestNumber}")
    fun signUp(
        @PathVariable contestNumber: Int,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<HttpStatus> {
        tokenService.extractEmailFromToken(jwt)
            .let { contestService.signUpToContest(it, contestNumber) }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/all")
    fun getAllContestsSortedByContestNumber(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<ContestPageDto> =
        contestService.getAllContestsSorted(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToContestPageDto(it)) }

    @GetMapping("/active")
    fun getActiveContests(): ResponseEntity<ContestsResponse> =
        contestService.getActiveContests()
            .map { mapToContestDto(it) }
            .let { ResponseEntity.ok(ContestsResponse(it)) }

    @GetMapping("/registered")
    fun getActiveContestsSignedUp(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<ContestsResponse> =
        tokenService.extractEmailFromToken(jwt)
            .let { contestService.getActiveContestsSignedUp(it) }
            .map { mapToContestDto(it) }
            .let { ResponseEntity.ok(ContestsResponse(it)) }

    @GetMapping("/unregistered")
    fun getActiveContestsNotSignedUp(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<ContestsResponse> =
        tokenService.extractEmailFromToken(jwt)
            .let { contestService.getActiveContestsNotSignedUp(it) }
            .map { mapToContestDto(it) }
            .let { ResponseEntity.ok(ContestsResponse(it)) }

    @GetMapping("/{contestNumber}")
    fun getContest(@PathVariable contestNumber: Int): ResponseEntity<ContestDto> =
        contestService.findByContestNumber(contestNumber)
            .let { ResponseEntity.ok(mapToContestDto(it)) }

    @PostMapping("/create")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<HttpStatus> =
        contestService.createContest(request.contestNumber, request.startTime)
            .let { ResponseEntity(HttpStatus.OK) }

    @PatchMapping("/update")
    fun updateContest(@RequestBody request: UpdateContestRequest): ResponseEntity<HttpStatus> =
        contestService.updateContest(request.contestNumber, request.contestStatus, request.startTime)
            .let { ResponseEntity(HttpStatus.OK) }

    @DeleteMapping("/{contestNumber}")
    fun deleteContest(@PathVariable contestNumber: Int): ResponseEntity<HttpStatus> =
        contestService.deleteContest(contestNumber)
            .let { ResponseEntity(HttpStatus.OK) }

    data class CreateContestRequest(
        val contestNumber: Int,
        val startTime: LocalDateTime
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class UpdateContestRequest(
        val startTime: LocalDateTime,
        val contestNumber: Int,
        val contestStatus: ContestStatus
    )

    data class ContestsResponse(
        val contests: List<ContestDto>
    )
}