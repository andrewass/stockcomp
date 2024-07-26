package com.stockcomp.contest.internal

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestPageDto
import com.stockcomp.contest.mapToContestDto
import com.stockcomp.contest.mapToContestPageDto
import com.stockcomp.exception.CustomExceptionHandler
import com.stockcomp.token.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/contests")
class ContestController(
    private val contestService: ContestServiceInternal
) : CustomExceptionHandler() {

    @GetMapping("/all")
    fun getAllContestsSortedByContestId(
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

    @GetMapping("/{contestId}")
    fun getContest(@PathVariable contestId: Long): ResponseEntity<ContestDto> =
        contestService.findByContestId(contestId)
            .let { ResponseEntity.ok(mapToContestDto(it)) }

    @PostMapping("/create")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<HttpStatus> =
        contestService.createContest(contestName = request.contestName, startTime = request.startTime)
            .let { ResponseEntity(HttpStatus.OK) }

    @PatchMapping("/update")
    fun updateContest(@RequestBody request: UpdateContestRequest): ResponseEntity<HttpStatus> =
        contestService.updateContest(request.contestId, request.contestStatus, request.startTime)
            .let { ResponseEntity(HttpStatus.OK) }

    @DeleteMapping("/{contestId}")
    fun deleteContest(@PathVariable contestId: Long): ResponseEntity<HttpStatus> =
        contestService.deleteContest(contestId)
            .let { ResponseEntity(HttpStatus.OK) }

    data class CreateContestRequest(
        val contestName: String,
        val startTime: LocalDateTime
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class UpdateContestRequest(
        val startTime: LocalDateTime,
        val contestId: Long,
        val contestStatus: ContestStatus
    )

    data class ContestsResponse(
        val contests: List<ContestDto>
    )
}