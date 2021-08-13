package com.stockcomp.response

import java.time.LocalDateTime

data class ContestDto(
    val startTime: LocalDateTime,

    val contestNumber: Int,

    var inPreStartMode: Boolean,

    var inRunningMode: Boolean
)
