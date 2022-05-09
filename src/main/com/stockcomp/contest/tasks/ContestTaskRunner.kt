package com.stockcomp.contest.tasks

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["application.runner.enabled"], matchIfMissing = true)
class ContestTaskRunner(
    private val contestTasks: ContestTasks
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        contestTasks.startOrderProcessing()
        contestTasks.startMaintainInvestments()
    }
}