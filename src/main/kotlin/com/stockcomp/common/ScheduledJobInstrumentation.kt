package com.stockcomp.common

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ScheduledJobInstrumentation(
    private val meterRegistry: MeterRegistry,
) {
    private val logger = LoggerFactory.getLogger(ScheduledJobInstrumentation::class.java)

    fun record(
        jobName: String,
        block: () -> ScheduledJobRunResult,
    ): ScheduledJobRunResult {
        val sample = Timer.start(meterRegistry)
        var result = ScheduledJobRunResult.success()
        var thrown: Exception? = null

        try {
            result = block()
            return result
        } catch (e: Exception) {
            result = ScheduledJobRunResult.failure()
            thrown = e
            throw e
        } finally {
            Counter
                .builder(RUNS_METRIC)
                .description("Scheduled job run count")
                .tag(JOB_TAG, jobName)
                .tag(OUTCOME_TAG, result.outcome.tagValue)
                .register(meterRegistry)
                .increment()

            val durationNanos =
                sample.stop(
                    Timer
                        .builder(DURATION_METRIC)
                        .description("Scheduled job run duration")
                        .tag(JOB_TAG, jobName)
                        .tag(OUTCOME_TAG, result.outcome.tagValue)
                        .register(meterRegistry),
                )
            val durationMs = TimeUnit.NANOSECONDS.toMillis(durationNanos)

            incrementItemCounter(jobName, "processed", result.processedItems)
            incrementItemCounter(jobName, "failed", result.failedItems)
            incrementItemCounter(jobName, "skipped", result.skippedItems)
            logRun(
                jobName = jobName,
                result = result,
                durationMs = durationMs,
                thrown = thrown,
            )
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

    private fun logRun(
        jobName: String,
        result: ScheduledJobRunResult,
        durationMs: Long,
        thrown: Exception?,
    ) {
        val message = "scheduled_job_run job={} outcome={} processedItems={} failedItems={} skippedItems={} durationMs={}"
        when (result.outcome) {
            ScheduledJobRunOutcome.SUCCESS -> {
                logger.info(
                    message,
                    jobName,
                    result.outcome.tagValue,
                    result.processedItems,
                    result.failedItems,
                    result.skippedItems,
                    durationMs,
                )
            }

            ScheduledJobRunOutcome.PARTIAL_FAILURE -> {
                logger.warn(
                    message,
                    jobName,
                    result.outcome.tagValue,
                    result.processedItems,
                    result.failedItems,
                    result.skippedItems,
                    durationMs,
                )
            }

            ScheduledJobRunOutcome.FAILURE -> {
                if (thrown == null) {
                    logger.error(
                        message,
                        jobName,
                        result.outcome.tagValue,
                        result.processedItems,
                        result.failedItems,
                        result.skippedItems,
                        durationMs,
                    )
                } else {
                    logger.error(
                        message,
                        jobName,
                        result.outcome.tagValue,
                        result.processedItems,
                        result.failedItems,
                        result.skippedItems,
                        durationMs,
                        thrown,
                    )
                }
            }

            ScheduledJobRunOutcome.SKIPPED -> {
                logger.debug(
                    message,
                    jobName,
                    result.outcome.tagValue,
                    result.processedItems,
                    result.failedItems,
                    result.skippedItems,
                    durationMs,
                )
            }
        }
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
    val failedItems: Int = 0,
    val skippedItems: Int = 0,
) {
    init {
        require(processedItems >= 0) { "Processed item count cannot be negative" }
        require(failedItems >= 0) { "Failed item count cannot be negative" }
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
            failedItems: Int = 0,
            skippedItems: Int = 0,
        ) = ScheduledJobRunResult(
            outcome = ScheduledJobRunOutcome.FAILURE,
            processedItems = processedItems,
            failedItems = failedItems,
            skippedItems = skippedItems,
        )

        fun partialFailure(
            processedItems: Int,
            failedItems: Int,
            skippedItems: Int = 0,
        ) = ScheduledJobRunResult(
            outcome = ScheduledJobRunOutcome.PARTIAL_FAILURE,
            processedItems = processedItems,
            failedItems = failedItems,
            skippedItems = skippedItems,
        )

        fun skipped(skippedItems: Int = 1) =
            ScheduledJobRunResult(
                outcome = ScheduledJobRunOutcome.SKIPPED,
                skippedItems = skippedItems,
            )

        fun fromItemCounts(
            processedItems: Int,
            failedItems: Int,
            skippedItems: Int = 0,
        ): ScheduledJobRunResult =
            when {
                processedItems == 0 && failedItems == 0 -> {
                    skipped(skippedItems = skippedItems.coerceAtLeast(1))
                }

                failedItems == 0 -> {
                    success(processedItems = processedItems, skippedItems = skippedItems)
                }

                processedItems == 0 -> {
                    failure(failedItems = failedItems, skippedItems = skippedItems)
                }

                else -> {
                    partialFailure(
                        processedItems = processedItems,
                        failedItems = failedItems,
                        skippedItems = skippedItems,
                    )
                }
            }
    }
}

enum class ScheduledJobRunOutcome(
    val tagValue: String,
) {
    SUCCESS("success"),
    FAILURE("failure"),
    PARTIAL_FAILURE("partial_failure"),
    SKIPPED("skipped"),
}
