package com.stockcomp.common

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ScheduledJobInstrumentationTest {
    private val meterRegistry = SimpleMeterRegistry()
    private val instrumentation = ScheduledJobInstrumentation(meterRegistry)

    @Test
    fun `should record successful job run metrics`() {
        val result =
            instrumentation.record(JOB_NAME) {
                ScheduledJobRunResult.success(
                    processedItems = 3,
                    skippedItems = 1,
                )
            }

        assertEquals(ScheduledJobRunOutcome.SUCCESS, result.outcome)
        assertEquals(1.0, runCounter("success"))
        assertEquals(3.0, itemCounter("processed"))
        assertEquals(1.0, itemCounter("skipped"))
        assertEquals(1L, timerCount("success"))
    }

    @Test
    fun `should record skipped job run metrics`() {
        instrumentation.record(JOB_NAME) {
            ScheduledJobRunResult.skipped()
        }

        assertEquals(1.0, runCounter("skipped"))
        assertEquals(1.0, itemCounter("skipped"))
        assertEquals(1L, timerCount("skipped"))
    }

    @Test
    fun `should record failed job run metrics when block throws`() {
        assertThrows(IllegalStateException::class.java) {
            instrumentation.record(JOB_NAME) {
                throw IllegalStateException("job failed")
            }
        }

        assertEquals(1.0, runCounter("failure"))
        assertEquals(1L, timerCount("failure"))
    }

    private fun runCounter(outcome: String): Double =
        meterRegistry
            .counter(
                ScheduledJobInstrumentation.RUNS_METRIC,
                "job",
                JOB_NAME,
                "outcome",
                outcome,
            ).count()

    private fun itemCounter(item: String): Double =
        meterRegistry
            .counter(
                ScheduledJobInstrumentation.ITEMS_METRIC,
                "job",
                JOB_NAME,
                "item",
                item,
            ).count()

    private fun timerCount(outcome: String): Long =
        requireNotNull(
            meterRegistry
                .find(ScheduledJobInstrumentation.DURATION_METRIC)
                .tag("job", JOB_NAME)
                .tag("outcome", outcome)
                .timer(),
        ).count()

    private companion object {
        const val JOB_NAME = "test-job"
    }
}
