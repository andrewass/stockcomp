package com.stockcomp.domain.contest.enums

enum class LeaderboardUpdateStatus(val decode : String) {
    AWAITING("Awaiting"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}