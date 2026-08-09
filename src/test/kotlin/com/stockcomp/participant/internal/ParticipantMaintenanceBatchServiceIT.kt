package com.stockcomp.participant.internal

import com.stockcomp.configuration.ControllerIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

@ControllerIntegrationTest
class ParticipantMaintenanceBatchServiceIT
    @Autowired
    constructor(
        private val participantMaintenanceBatchService: ParticipantMaintenanceBatchService,
        private val jdbcTemplate: JdbcTemplate,
    ) {
        @Test
        fun `should select bounded batches and eventually process every participant`() {
            val contestId = insertRunningContest()
            val participantIds = List(5) { index -> insertParticipant(contestId, index) }

            val firstBatch = nextBatch(contestId)
            assertEquals(participantIds.take(2), firstBatch)
            advanceCursor(firstBatch)

            val secondBatch = nextBatch(contestId)
            assertEquals(participantIds.slice(2..3), secondBatch)
            advanceCursor(secondBatch)

            val thirdBatch = nextBatch(contestId)
            assertEquals(listOf(participantIds[4], participantIds[0]), thirdBatch)
        }

        private fun nextBatch(contestId: Long): List<Long> =
            participantMaintenanceBatchService.getNextParticipantIds(
                jobName = JOB_NAME,
                contestIds = listOf(contestId),
                maxParticipants = BATCH_SIZE,
            )

        private fun advanceCursor(participantIds: List<Long>) {
            participantMaintenanceBatchService.advanceCursor(JOB_NAME, participantIds.last())
        }

        private fun insertRunningContest(): Long =
            jdbcTemplate.queryForObject(
                """
                insert into t_contest (contest_name, start_time, end_time, contest_status, date_created, date_updated)
                values (?, ?, ?, 'RUNNING', current_timestamp, current_timestamp)
                returning contest_id
                """.trimIndent(),
                Long::class.java,
                "Maintenance cursor contest",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
            )!!

        private fun insertParticipant(
            contestId: Long,
            index: Int,
        ): Long {
            val userId =
                jdbcTemplate.queryForObject(
                    """
                    insert into t_user (username, email, user_role, user_status, date_created, date_updated)
                    values (?, ?, 'USER', 'ACTIVE', current_timestamp, current_timestamp)
                    returning user_id
                    """.trimIndent(),
                    Long::class.java,
                    "maintenance-cursor-user-$index",
                    "maintenance-cursor-user-$index@test.com",
                )!!
            return jdbcTemplate.queryForObject(
                """
                insert into t_participant (
                    contest_id,
                    user_id,
                    remaining_funds,
                    total_value,
                    total_investment_value,
                    date_created,
                    date_updated
                )
                values (?, ?, 20000, 20000, 0, current_timestamp, current_timestamp)
                returning participant_id
                """.trimIndent(),
                Long::class.java,
                contestId,
                userId,
            )!!
        }

        private companion object {
            const val BATCH_SIZE = 2
            const val JOB_NAME = "participant-maintenance-batch-test"
        }
    }
