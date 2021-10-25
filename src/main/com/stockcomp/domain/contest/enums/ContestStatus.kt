package com.stockcomp.domain.contest.enums

enum class ContestStatus(val decode : String) {
    AWAITING_START("Awaiting Start"),
    RUNNING("Running"),
    STOPPED("Stopped"),
    COMPLETED("Completed")
}