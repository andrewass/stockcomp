package com.stockcomp.contest.entity

enum class ContestStatus(val decode: String) {
    AWAITING_START("Awaiting Start"),
    RUNNING("Running"),
    STOPPED("Stopped"),
    COMPLETED("Completed");

    companion object {
        private val map = values().associateBy(ContestStatus::decode)

        fun fromDecode(decode: String) = map[decode]
    }
}