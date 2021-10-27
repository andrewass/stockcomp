package com.stockcomp.domain.contest.enums

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