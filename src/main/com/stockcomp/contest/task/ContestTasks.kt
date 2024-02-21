package com.stockcomp.contest.task

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ContestTasks {

    @Scheduled(fixedRate = 30000)
    fun runMaintainContests() {
        println("placeholder contests")
    }
}