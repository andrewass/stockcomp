package com.stockcomp.leaderboard.internal

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPatchRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.leaderboard.LeaderboardEntryDto
import com.stockcomp.leaderboard.LeaderboardEntryPageDto
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntryRepository
import com.stockcomp.leaderboard.internal.job.JobStatus
import com.stockcomp.leaderboard.internal.job.LeaderboardJob
import com.stockcomp.leaderboard.internal.job.LeaderboardJobRepository
import com.stockcomp.leaderboard.internal.job.LeaderboardJobScheduler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@ControllerIntegrationTest
class LeaderboardOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val leaderboardRepository: LeaderboardRepository,
        private val leaderboardEntryRepository: LeaderboardEntryRepository,
        private val leaderboardJobRepository: LeaderboardJobRepository,
        private val leaderboardJobScheduler: LeaderboardJobScheduler,
        private val jdbcTemplate: JdbcTemplate,
    ) {
        private val mapper = jacksonObjectMapper()
        private val basePath = "/leaderboard"

        @Test
        fun `should return sorted leaderboard entries`() {
            val leaderboard = getOrCreateDefaultLeaderboard()
            val user = createUser()
            leaderboardEntryRepository.save(LeaderboardEntry(leaderboard = leaderboard, userId = user.userId))

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest(basePath)
                            .queryParam("pageNumber", "0")
                            .queryParam("pageSize", "5"),
                    ).andExpect(status().isOk)
                    .andReturn()

            val response: LeaderboardEntryPageDto = mapper.readValue(result.response.contentAsString)
            assertEquals(1L, response.totalEntriesCount)
            assertTrue(response.entries.isNotEmpty())
        }

        @Test
        fun `should return leaderboard entry for user`() {
            val leaderboard = getOrCreateDefaultLeaderboard()
            val user = createUser()
            leaderboardEntryRepository.save(LeaderboardEntry(leaderboard = leaderboard, userId = user.userId))

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/me", emailClaim = user.email))
                    .andExpect(status().isOk)
                    .andReturn()

            val response: LeaderboardEntryDto = mapper.readValue(result.response.contentAsString)
            assertEquals(0, response.score)
        }

        @Test
        fun `should update leaderboard`() {
            val contestId = createContestAwaitingCompletion("ContestForApiUpdate")

            mockMvc
                .perform(
                    mockMvcPostRequest("$basePath/recalculations", "ADMIN")
                        .queryParam("contestId", contestId.toString()),
                ).andExpect(status().isNoContent)

            val contestResult =
                mockMvc
                    .perform(mockMvcGetRequest("/contests/$contestId"))
                    .andExpect(status().isOk)
                    .andReturn()
            assertEquals("COMPLETED", mapper.readTree(contestResult.response.contentAsString)["contestStatus"].asText())
        }

        @Test
        fun `should rank ties award medals and remain idempotent`() {
            val contestId = createContestAwaitingCompletion("ContestWithRankings")
            val first = createUser()
            val second = createUser()
            val third = createUser()
            val fourth = createUser()
            insertParticipant(first.userId, contestId, BigDecimal("25000.00"))
            insertParticipant(second.userId, contestId, BigDecimal("25000.00"))
            insertParticipant(third.userId, contestId, BigDecimal("23000.00"))
            insertParticipant(fourth.userId, contestId, BigDecimal("21000.00"))

            repeat(2) {
                mockMvc
                    .perform(
                        mockMvcPostRequest("$basePath/recalculations", "ADMIN")
                            .queryParam("contestId", contestId.toString()),
                    ).andExpect(status().isNoContent)
            }

            assertEquals(1, participantRank(first.userId, contestId))
            assertEquals(1, participantRank(second.userId, contestId))
            assertEquals(3, participantRank(third.userId, contestId))
            assertEquals(4, participantRank(fourth.userId, contestId))
            assertLeaderboard(first.userId, ranking = 1, score = 3, contestCount = 1)
            assertLeaderboard(second.userId, ranking = 1, score = 3, contestCount = 1)
            assertLeaderboard(third.userId, ranking = 3, score = 1, contestCount = 1)
            assertLeaderboard(fourth.userId, ranking = 4, score = 0, contestCount = 1)
            assertEquals(
                3,
                jdbcTemplate.queryForObject(
                    "select count(*) from t_medal where contest_id = ?",
                    Int::class.java,
                    contestId,
                ),
            )
        }

        @Test
        fun `should return forbidden when non-admin updates leaderboard`() {
            mockMvc
                .perform(
                    mockMvcPostRequest("$basePath/recalculations", "USER")
                        .queryParam("contestId", "1"),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should create only one open leaderboard job per contest`() {
            val contestId = createContestAwaitingCompletion("ContestForJobCreation")

            leaderboardJobScheduler.createLeaderboardJobs()
            leaderboardJobScheduler.createLeaderboardJobs()

            assertEquals(1L, leaderboardJobRepository.countByContestId(contestId))
            assertTrue(
                leaderboardJobRepository.existsByContestIdAndJobStatusIn(
                    contestId,
                    listOf(JobStatus.CREATED),
                ),
            )
        }

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        fun `should mark contest as completed when leaderboard job is processed`() {
            val contestId = createContestAwaitingCompletion("ContestForJobProcessing")
            leaderboardJobRepository.save(LeaderboardJob(contestId = contestId))

            leaderboardJobScheduler.processLeaderboardJob()

            val contestResult =
                mockMvc
                    .perform(mockMvcGetRequest("/contests/$contestId"))
                    .andExpect(status().isOk)
                    .andReturn()

            val contestStatus = mapper.readTree(contestResult.response.contentAsString)["contestStatus"].asText()
            assertEquals("COMPLETED", contestStatus)
            assertTrue(
                !leaderboardJobRepository.existsByContestIdAndJobStatusIn(
                    contestId,
                    listOf(JobStatus.CREATED, JobStatus.FAILED),
                ),
            )
        }

        @Test
        fun `should return not found when leaderboard entry for user does not exist`() {
            val email = "missing-${UUID.randomUUID().toString().take(12)}@test.com"
            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/me", emailClaim = email))
                    .andExpect(status().isNotFound)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            assertEquals(404, response["status"].asInt())
            assertEquals("Leaderboard entry not found", response["title"].asText())
            assertEquals("/problems/leaderboard/not-found", response["type"].asText())
        }

        @Test
        fun `should return bad request for invalid pagination parameters`() {
            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest(basePath)
                            .queryParam("pageNumber", "-1")
                            .queryParam("pageSize", "0"),
                    ).andExpect(status().isBadRequest)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            assertEquals(400, response["status"].asInt())
            assertTrue(response["title"].asText().isNotBlank())
        }

        private fun createContestAwaitingCompletion(contestName: String): Long {
            val createPayload =
                """
                {
                  "contestName": "$contestName",
                  "startTime": "${LocalDateTime.now().plusDays(2)}",
                  "durationDays": 7
                }
                """.trimIndent()

            val createResult =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/contests", "ADMIN")
                            .content(createPayload),
                    ).andExpect(status().isCreated)
                    .andReturn()

            val contestId = mapper.readTree(createResult.response.contentAsString)["contestId"].asLong()

            mockMvc
                .perform(
                    mockMvcPatchRequest("/contests/$contestId", "ADMIN")
                        .content(
                            """
                            {
                              "contestStatus": "AWAITING_COMPLETION"
                            }
                            """.trimIndent(),
                        ),
                ).andExpect(status().isOk)

            return contestId
        }

        private fun getOrCreateDefaultLeaderboard(): Leaderboard =
            leaderboardRepository
                .findById(1L)
                .orElseGet { leaderboardRepository.save(Leaderboard(leaderboardId = 1L)) }

        private fun createUser(): CreatedUser {
            val email = "leaderboard-${UUID.randomUUID()}@test.com"
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/users", "ADMIN")
                            .content("""{"email":"$email"}"""),
                    ).andExpect(status().isCreated)
                    .andReturn()

            return CreatedUser(
                userId = mapper.readTree(result.response.contentAsString)["userId"].asLong(),
                email = email,
            )
        }

        private fun insertParticipant(
            userId: Long,
            contestId: Long,
            totalValue: BigDecimal,
        ) {
            jdbcTemplate.update(
                """
                insert into t_participant (
                    contest_id,
                    user_id,
                    remaining_funds,
                    participant_rank,
                    total_value,
                    total_investment_value,
                    date_created,
                    date_updated,
                    version
                )
                values (?, ?, ?, null, ?, 0, current_timestamp, current_timestamp, 0)
                """.trimIndent(),
                contestId,
                userId,
                totalValue,
                totalValue,
            )
        }

        private fun participantRank(
            userId: Long,
            contestId: Long,
        ): Int =
            jdbcTemplate.queryForObject(
                "select participant_rank from t_participant where user_id = ? and contest_id = ?",
                Int::class.java,
                userId,
                contestId,
            )!!

        private fun assertLeaderboard(
            userId: Long,
            ranking: Int,
            score: Int,
            contestCount: Int,
        ) {
            val values =
                jdbcTemplate.queryForMap(
                    """
                    select ranking, score, contest_count
                    from t_leaderboard_entry
                    where user_id = ?
                    """.trimIndent(),
                    userId,
                )
            assertEquals(ranking, values["ranking"])
            assertEquals(score, values["score"])
            assertEquals(contestCount, values["contest_count"])
        }

        private data class CreatedUser(
            val userId: Long,
            val email: String,
        )
    }
