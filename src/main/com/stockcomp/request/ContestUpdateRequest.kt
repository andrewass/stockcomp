package com.stockcomp.request

import com.stockcomp.domain.contest.enums.ContestStatus
import java.time.LocalDateTime

data class ContestUpdateRequest(
    val id: Long? = null,
    val startTime: LocalDateTime,
    val contestNumber: Int,
    val contestStatus: ContestStatus
)