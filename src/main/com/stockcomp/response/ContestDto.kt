package com.stockcomp.response

import java.time.LocalDateTime

data class ContestDto(

    val id : Long,

    val startTime: LocalDateTime,

    val contestNumber: Int,

    val running: Boolean,

    val completed: Boolean
)
