package com.stockcomp.configuration

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
class DatabaseInvariantIT
    @Autowired
    constructor(
        private val jdbcTemplate: JdbcTemplate,
    ) {
        @Test
        fun `should reject duplicate participant signup`() {
            val userId = insertUser("duplicate-participant@mail.com")
            val contestId = insertContest("Duplicate Participant Contest")
            insertParticipant(userId, contestId)

            assertThrows(DataIntegrityViolationException::class.java) {
                insertParticipant(userId, contestId)
            }
        }

        @Test
        fun `should reject duplicate investments for one participant and symbol`() {
            val userId = insertUser("duplicate-investment@mail.com")
            val contestId = insertContest("Duplicate Investment Contest")
            val participantId = insertParticipant(userId, contestId)
            insertInvestment(participantId)

            assertThrows(DataIntegrityViolationException::class.java) {
                insertInvestment(participantId)
            }
        }

        @Test
        fun `should delete contest-owned data when its contest is deleted`() {
            val userId = insertUser("contest-owned-data@mail.com")
            val contestId = insertContest("Leaderboard Job Contest")
            val participantId = insertParticipant(userId, contestId)

            assertThrows(DataIntegrityViolationException::class.java) {
                insertLeaderboardJob(Long.MAX_VALUE)
            }

            insertLeaderboardJob(contestId)
            insertInvestment(participantId)
            insertInvestmentOrder(
                participantId = participantId,
                totalAmount = 1,
                remainingAmount = 1,
                acceptedPrice = BigDecimal("100.00"),
            )

            jdbcTemplate.update("delete from t_contest where contest_id = ?", contestId)

            assertRowCount("t_participant", "contest_id", contestId, expectedCount = 0)
            assertRowCount("t_investment", "participant_id", participantId, expectedCount = 0)
            assertRowCount("t_investment_order", "participant_id", participantId, expectedCount = 0)
            assertRowCount("t_leaderboard_job", "contest_id", contestId, expectedCount = 0)
        }

        @Test
        fun `should enforce a singleton leaderboard`() {
            assertThrows(DataIntegrityViolationException::class.java) {
                jdbcTemplate.update(
                    """
                    insert into t_leaderboard (contest_count, date_created, date_updated, version)
                    values (0, current_timestamp, current_timestamp, 0)
                    """.trimIndent(),
                )
            }
        }

        @Test
        fun `should reject invalid enum values and time ranges`() {
            assertThrows(DataIntegrityViolationException::class.java) {
                jdbcTemplate.update(
                    """
                    insert into t_contest (contest_name, start_time, end_time, contest_status, date_created, date_updated)
                    values ('Invalid Contest Status', current_timestamp, current_timestamp + interval '1 day', 'INVALID', current_timestamp, current_timestamp)
                    """.trimIndent(),
                )
            }
            assertThrows(DataIntegrityViolationException::class.java) {
                jdbcTemplate.update(
                    """
                    insert into t_contest (contest_name, start_time, end_time, contest_status, date_created, date_updated)
                    values ('Invalid Contest Time Range', current_timestamp, current_timestamp, 'AWAITING_START', current_timestamp, current_timestamp)
                    """.trimIndent(),
                )
            }
        }

        @Test
        fun `should reject invalid participant and order amounts`() {
            val userId = insertUser("invalid-amounts@mail.com")
            val contestId = insertContest("Invalid Amounts Contest")

            assertThrows(DataIntegrityViolationException::class.java) {
                insertParticipant(userId, contestId, remainingFunds = BigDecimal("-1.00"))
            }

            val participantId = insertParticipant(userId, contestId)
            assertThrows(DataIntegrityViolationException::class.java) {
                insertInvestmentOrder(participantId, totalAmount = 0, remainingAmount = 0, acceptedPrice = BigDecimal("100.00"))
            }
            assertThrows(DataIntegrityViolationException::class.java) {
                insertInvestmentOrder(participantId, totalAmount = 10, remainingAmount = 11, acceptedPrice = BigDecimal("100.00"))
            }
            assertThrows(DataIntegrityViolationException::class.java) {
                insertInvestmentOrder(participantId, totalAmount = 10, remainingAmount = 10, acceptedPrice = BigDecimal.ZERO)
            }
        }

        @Test
        fun `should reject an active external identity linked to multiple users`() {
            val firstUserId = insertUser("first-identity@mail.com")
            val secondUserId = insertUser("second-identity@mail.com")
            insertUserSubject(firstUserId, "GOOGLE", "google-subject")

            assertThrows(DataIntegrityViolationException::class.java) {
                insertUserSubject(secondUserId, "GOOGLE", "google-subject")
            }
        }

        @Test
        fun `should include indexes for foreign keys and common scheduler lookups`() {
            assertIndex("uq_t_participant_user_contest", "(user_id, contest_id)")
            assertIndex("idx_t_participant_contest_rank", "(contest_id, participant_rank)")
            assertIndex("idx_t_participant_contest_total_value", "(contest_id, total_value desc)")
            assertIndex("idx_t_leaderboard_entry_user_id", "(user_id)")
            assertIndex("uq_t_leaderboard_entry_user", "(user_id)")
            assertIndex("idx_t_leaderboard_entry_leaderboard_id", "(leaderboard_id)")
            assertIndex("idx_t_medal_contest_id", "(contest_id)")
            assertIndex("idx_t_medal_leaderboard_entry_id", "(leaderboard_entry_id)")
            assertIndex("uq_t_medal_entry_contest", "(leaderboard_entry_id, contest_id)")
            assertIndex("uq_t_leaderboard_singleton_key", "(singleton_key)")
            assertIndex("uq_t_investment_participant_symbol", "(participant_id, symbol)")
            assertIndex("idx_t_investment_order_participant_status_symbol", "(participant_id, order_status, symbol)")
            assertIndex("idx_t_refresh_token_user_id", "(user_id)")
            assertIndex("uk_user_subject", "(user_id, subject_provider, external_subject_id)")
            assertIndex("uq_t_user_subject_active_external_identity", "(subject_provider, external_subject_id)")
            assertIndex("idx_t_leaderboard_job_status_next_run_at", "(job_status, next_run_at)")
            assertIndex("idx_t_leaderboard_job_contest_id", "(contest_id)")
            assertConstraint("uq_t_leaderboard_singleton_key")
            assertConstraint("uq_t_investment_participant_symbol")
            assertConstraint("fk_t_leaderboard_job_contest")
            assertConstraint("fk_t_participant_contest")
            assertConstraint("fk_t_investment_participant")
            assertConstraint("fk_t_investment_order_participant")
            assertConstraint("fk_t_medal_contest")
            assertConstraint("ck_t_user_role_known")
            assertConstraint("ck_t_user_status_known")
            assertConstraint("ck_t_user_subject_provider_known")
            assertConstraint("ck_t_contest_status_known")
            assertConstraint("ck_t_contest_valid_time_range")
            assertConstraint("ck_t_investment_order_transaction_type_known")
            assertConstraint("ck_t_investment_order_status_known")
            assertConstraint("ck_t_investment_order_expiration_after_creation")
            assertConstraint("ck_t_leaderboard_job_status_known")
            assertConstraint("ck_t_medal_value_known")
            assertConstraint("ck_t_medal_position_matches_value")
        }

        private fun assertIndex(
            indexName: String,
            expectedColumns: String,
        ) {
            val indexDefinition =
                jdbcTemplate.queryForObject(
                    """
                    select indexdef
                    from pg_indexes
                    where schemaname = 'public'
                    and indexname = ?
                    """.trimIndent(),
                    String::class.java,
                    indexName,
                )

            assertNotNull(indexDefinition, "Expected index $indexName to exist")
            assertTrue(
                indexDefinition!!.lowercase().contains(expectedColumns),
                "Expected $indexName to cover $expectedColumns, but definition was $indexDefinition",
            )
        }

        private fun assertConstraint(constraintName: String) {
            val constraintExists =
                jdbcTemplate.queryForObject(
                    """
                    select exists (
                        select 1
                        from pg_constraint
                        where conname = ?
                    )
                    """.trimIndent(),
                    Boolean::class.java,
                    constraintName,
                )

            assertTrue(constraintExists == true, "Expected constraint $constraintName to exist")
        }

        private fun assertRowCount(
            tableName: String,
            columnName: String,
            referenceId: Long,
            expectedCount: Int,
        ) {
            val actualCount =
                jdbcTemplate.queryForObject(
                    "select count(*) from $tableName where $columnName = ?",
                    Int::class.java,
                    referenceId,
                )

            assertTrue(actualCount == expectedCount, "Expected $expectedCount rows in $tableName but found $actualCount")
        }

        private fun insertUser(email: String): Long =
            jdbcTemplate.queryForObject(
                """
                insert into t_user (username, email, user_role, user_status, date_created, date_updated)
                values (?, ?, 'USER', 'ACTIVE', current_timestamp, current_timestamp)
                returning user_id
                """.trimIndent(),
                Long::class.java,
                email.substringBefore("@"),
                email,
            )!!

        private fun insertContest(contestName: String): Long =
            jdbcTemplate.queryForObject(
                """
                insert into t_contest (contest_name, start_time, end_time, contest_status, date_created, date_updated)
                values (?, current_timestamp, current_timestamp + interval '1 day', 'COMPLETED', current_timestamp, current_timestamp)
                returning contest_id
                """.trimIndent(),
                Long::class.java,
                contestName,
            )!!

        private fun insertUserSubject(
            userId: Long,
            provider: String,
            externalSubjectId: String,
        ) {
            jdbcTemplate.update(
                """
                insert into t_user_subject (
                    user_id,
                    subject_provider,
                    external_subject_id,
                    is_valid,
                    date_created,
                    date_updated,
                    version
                )
                values (?, ?, ?, true, current_timestamp, current_timestamp, 0)
                """.trimIndent(),
                userId,
                provider,
                externalSubjectId,
            )
        }

        private fun insertParticipant(
            userId: Long,
            contestId: Long,
            remainingFunds: BigDecimal = BigDecimal("20000.00"),
        ): Long =
            jdbcTemplate.queryForObject(
                """
                insert into t_participant (
                    contest_id,
                    user_id,
                    remaining_funds,
                    participant_rank,
                    total_value,
                    total_investment_value,
                    date_created,
                    date_updated
                )
                values (?, ?, ?, null, ?, 0, current_timestamp, current_timestamp)
                returning participant_id
                """.trimIndent(),
                Long::class.java,
                contestId,
                userId,
                remainingFunds,
                remainingFunds,
            )!!

        private fun insertInvestmentOrder(
            participantId: Long,
            totalAmount: Int,
            remainingAmount: Int,
            acceptedPrice: BigDecimal,
        ) {
            jdbcTemplate.update(
                """
                insert into t_investment_order (
                    symbol,
                    total_amount,
                    remaining_amount,
                    accepted_price,
                    currency,
                    expiration_time,
                    transaction_type,
                    order_status,
                    participant_id,
                    date_created,
                    date_updated
                )
                values ('AAPL', ?, ?, ?, 'USD', current_timestamp + interval '1 day', 'BUY', 'ACTIVE', ?, current_timestamp, current_timestamp)
                """.trimIndent(),
                totalAmount,
                remainingAmount,
                acceptedPrice,
                participantId,
            )
        }

        private fun insertInvestment(participantId: Long) {
            jdbcTemplate.update(
                """
                insert into t_investment (
                    symbol,
                    amount,
                    average_unit_cost,
                    total_profit,
                    total_value,
                    participant_id,
                    date_created,
                    date_updated
                )
                values ('AAPL', 1, 100.00, 0, 100.00, ?, current_timestamp, current_timestamp)
                """.trimIndent(),
                participantId,
            )
        }

        private fun insertLeaderboardJob(contestId: Long) {
            jdbcTemplate.update(
                """
                insert into t_leaderboard_job (
                    contest_id,
                    job_status,
                    next_run_at,
                    attempts,
                    date_created,
                    date_updated,
                    version
                )
                values (?, 'CREATED', current_timestamp, 0, current_timestamp, current_timestamp, 0)
                """.trimIndent(),
                contestId,
            )
        }
    }
