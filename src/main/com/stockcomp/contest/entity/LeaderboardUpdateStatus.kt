package com.stockcomp.contest.entity

enum class LeaderboardUpdateStatus(val decode : String) {
    AWAITING("Awaiting"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}