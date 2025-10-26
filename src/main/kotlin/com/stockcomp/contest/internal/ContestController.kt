package com.stockcomp.contest.internal

import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestPageDto
import com.stockcomp.contest.mapToContestPageDto
import com.stockcomp.contest.toContestDto
import com.stockcomp.exception.CustomExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/contests")
class ContestController(
    private val contestService: ContestService,
) : CustomExceptionHandler() {
    @GetMapping("/all")
    fun getAllContestsSortedByContestId(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): ResponseEntity<ContestPageDto> =
        contestService
            .getAllContestsSorted(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToContestPageDto(it)) }

    @GetMapping("/exists-active")
    fun existsActiveContest(): ResponseEntity<ExistsActiveContestResponse> =
        ResponseEntity.ok(ExistsActiveContestResponse(contestService.existsActiveContest()))

    @GetMapping("/active")
    fun getActiveContests(): ResponseEntity<ContestsResponse> =
        contestService
            .getActiveContests()
            .map { toContestDto(it) }
            .let { ResponseEntity.ok(ContestsResponse(it)) }

    @GetMapping("/{contestId}")
    fun getContest(
        @PathVariable contestId: Long,
    ): ResponseEntity<ContestDto> =
        contestService
            .findByContestId(contestId)
            .let { ResponseEntity.ok(toContestDto(it)) }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    fun createContest(
        @RequestBody request: CreateContestRequest,
    ): ResponseEntity<HttpStatus> =
        contestService
            .createContest(contestName = request.contestName, startTime = request.startTime)
            .let { ResponseEntity(HttpStatus.OK) }

    @PatchMapping("/update")
    fun updateContest(
        @RequestBody request: UpdateContestRequest,
    ): ResponseEntity<HttpStatus> =
        contestService
            .updateContest(request.contestId, request.contestName, request.contestStatus, request.startTime)
            .let { ResponseEntity(HttpStatus.OK) }

    @DeleteMapping("/{contestId}")
    fun deleteContest(
        @PathVariable contestId: Long,
    ): ResponseEntity<HttpStatus> =
        contestService
            .deleteContest(contestId)
            .let { ResponseEntity(HttpStatus.OK) }

    data class CreateContestRequest(
        val contestName: String,
        val startTime: LocalDateTime,
        val duration: Int,
    )

    data class UpdateContestRequest(
        val startTime: LocalDateTime,
        val contestId: Long,
        val contestName: String,
        val contestStatus: ContestStatus,
    )

    data class ExistsActiveContestResponse(
        val existsActiveContests: Boolean,
    )

    data class ContestsResponse(
        val contests: List<ContestDto>,
    )
}
