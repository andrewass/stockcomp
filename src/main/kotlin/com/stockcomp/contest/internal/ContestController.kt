package com.stockcomp.contest.internal

import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestPageDto
import com.stockcomp.contest.ContestStatus
import com.stockcomp.contest.CreateContestRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
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

@Validated
@RestController
@RequestMapping("/contests")
class ContestController(
    private val contestService: ContestService,
) {
    @GetMapping
    fun getAllContestsSortedByContestId(
        @RequestParam @PositiveOrZero pageNumber: Int,
        @RequestParam @Positive @Max(100) pageSize: Int,
    ): ResponseEntity<ContestPageDto> = ResponseEntity.ok(mapToContestPageDto(contestService.getAllContestsSorted(pageNumber, pageSize)))

    @GetMapping("/active/exists")
    fun existsActiveContest(): ResponseEntity<ExistsActiveContestResponse> =
        ResponseEntity.ok(ExistsActiveContestResponse(contestService.existsActiveContest()))

    @GetMapping("/active")
    fun getActiveContests(): ResponseEntity<ContestsResponse> =
        ResponseEntity.ok(ContestsResponse(contestService.getActiveContests().map { toContestDto(it) }))

    @GetMapping("/{contestId}")
    fun getContest(
        @PathVariable @Positive contestId: Long,
    ): ResponseEntity<ContestDto> = ResponseEntity.ok(toContestDto(contestService.getContest(contestId)))

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createContest(
        @Valid @RequestBody request: CreateContestRequest,
    ): ResponseEntity<ContestDto> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                toContestDto(
                    contestService.createContest(
                        contestName = request.contestName,
                        startTime = request.startTime,
                        durationDays = request.durationDays,
                    ),
                ),
            )

    @PatchMapping("/{contestId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateContest(
        @PathVariable @Positive contestId: Long,
        @Valid @RequestBody request: UpdateContestRequest,
    ): ResponseEntity<ContestDto> =
        ResponseEntity.ok(
            toContestDto(contestService.updateContest(contestId, request.contestName, request.contestStatus, request.startTime)),
        )

    @DeleteMapping("/{contestId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteContest(
        @PathVariable @Positive contestId: Long,
    ): ResponseEntity<Void> {
        contestService.deleteContest(contestId)
        return ResponseEntity.noContent().build()
    }
}

data class UpdateContestRequest(
    val startTime: LocalDateTime? = null,
    @field:Pattern(regexp = ".*\\S.*", message = "contestName must not be blank")
    val contestName: String? = null,
    val contestStatus: ContestStatus? = null,
)

data class ExistsActiveContestResponse(
    val existsActiveContests: Boolean,
)

data class ContestsResponse(
    val contests: List<ContestDto>,
)
