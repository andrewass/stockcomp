package com.stockcomp.contest

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class ContestDto(
    val contestId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
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
    val startTime: LocalDateTime,
    @field:Positive
    val durationDays: Long,
)
