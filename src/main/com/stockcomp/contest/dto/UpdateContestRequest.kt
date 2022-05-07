package com.stockcomp.contest.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.stockcomp.contest.entity.ContestStatus
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateContestRequest(
    val startTime: LocalDateTime,
    val contestNumber: Int,
    val contestStatus: ContestStatus
)