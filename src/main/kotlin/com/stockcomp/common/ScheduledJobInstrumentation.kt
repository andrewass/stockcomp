package com.stockcomp.common

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Component

@Component
class ScheduledJobInstrumentation(
    private val meterRegistry: MeterRegistry,
) {
    fun record(
        jobName: String,
        block: () -> ScheduledJobRunResult,
    ): ScheduledJobRunResult {
        val sample = Timer.start(meterRegistry)
        var result = ScheduledJobRunResult.success()

        try {
            result = block()
            return result
        } catch (e: Exception) {
            result = ScheduledJobRunResult.failure()
            throw e
        } finally {
            Counter
                .builder(RUNS_METRIC)
                .description("Scheduled job run count")
                .tag(JOB_TAG, jobName)
                .tag(OUTCOME_TAG, result.outcome.tagValue)
                .register(meterRegistry)
                .increment()

            sample.stop(
                Timer
                    .builder(DURATION_METRIC)
                    .description("Scheduled job run duration")
                    .tag(JOB_TAG, jobName)
                    .tag(OUTCOME_TAG, result.outcome.tagValue)
                    .register(meterRegistry),
            )

            incrementItemCounter(jobName, "processed", result.processedItems)
            incrementItemCounter(jobName, "skipped", result.skippedItems)
        }
    }

    private fun incrementItemCounter(
        jobName: String,
        itemType: String,
        count: Int,
    ) {
        if (count == 0) {
            return
        }
        Counter
            .builder(ITEMS_METRIC)
            .description("Scheduled job item count")
            .tag(JOB_TAG, jobName)
            .tag(ITEM_TAG, itemType)
            .register(meterRegistry)
            .increment(count.toDouble())
    }

    companion object {
        const val RUNS_METRIC = "stockcomp.scheduled.job.runs"
        const val DURATION_METRIC = "stockcomp.scheduled.job.duration"
        const val ITEMS_METRIC = "stockcomp.scheduled.job.items"
        private const val JOB_TAG = "job"
        private const val OUTCOME_TAG = "outcome"
        private const val ITEM_TAG = "item"
    }
}

data class ScheduledJobRunResult(
    val outcome: ScheduledJobRunOutcome,
    val processedItems: Int = 0,
    val skippedItems: Int = 0,
) {
    init {
        require(processedItems >= 0) { "Processed item count cannot be negative" }
        require(skippedItems >= 0) { "Skipped item count cannot be negative" }
    }

    companion object {
        fun success(
            processedItems: Int = 0,
            skippedItems: Int = 0,
        ) = ScheduledJobRunResult(
            outcome = ScheduledJobRunOutcome.SUCCESS,
            processedItems = processedItems,
            skippedItems = skippedItems,
        )

        fun failure(
            processedItems: Int = 0,
            skippedItems: Int = 0,
        ) = ScheduledJobRunResult(
            outcome = ScheduledJobRunOutcome.FAILURE,
            processedItems = processedItems,
            skippedItems = skippedItems,
        )

        fun skipped(skippedItems: Int = 1) =
            ScheduledJobRunResult(
                outcome = ScheduledJobRunOutcome.SKIPPED,
                skippedItems = skippedItems,
            )
    }
}

enum class ScheduledJobRunOutcome(
    val tagValue: String,
) {
    SUCCESS("success"),
    FAILURE("failure"),
    SKIPPED("skipped"),
}
