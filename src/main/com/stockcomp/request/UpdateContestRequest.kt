package com.stockcomp.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.stockcomp.domain.contest.enums.ContestStatus
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateContestRequest(
    val startTime: LocalDateTime,
    val contestNumber: Int,
    val contestStatus: ContestStatus
)