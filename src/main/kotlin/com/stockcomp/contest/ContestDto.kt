package com.stockcomp.contest

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.Instant

data class ContestDto(
    val contestId: Long,
    val startTime: Instant,
    val endTime: Instant,
    val contestName: String,
    val contestStatus: ContestStatus,
)

data class ContestPageDto(
    val contests: List<ContestDto>,
    val totalEntriesCount: Long,
)

data class CreateContestRequest(
    @field:NotBlank
    val contestName: String,
    val startTime: Instant,
    @field:Positive
    val durationDays: Long,
)
