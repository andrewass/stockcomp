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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
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
    ) {
        private val mapper = jacksonObjectMapper()
        private val basePath = "/leaderboard"

        @Test
        fun `should return sorted leaderboard entries`() {
            val leaderboard = getOrCreateDefaultLeaderboard()
            val userId = createUser()
            leaderboardEntryRepository.save(LeaderboardEntry(leaderboard = leaderboard, userId = userId))

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/sorted")
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
            val userId = createUser()
            leaderboardEntryRepository.save(LeaderboardEntry(leaderboard = leaderboard, userId = userId))

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/user/$userId"))
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
                    mockMvcPostRequest("$basePath/update")
                        .queryParam("contestId", contestId.toString()),
                ).andExpect(status().isOk)

            val contestResult =
                mockMvc
                    .perform(mockMvcGetRequest("/contests/$contestId"))
                    .andExpect(status().isOk)
                    .andReturn()
            assertEquals("COMPLETED", mapper.readTree(contestResult.response.contentAsString)["contestStatus"].asText())
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
            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/user/999999"))
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
                        mockMvcGetRequest("$basePath/sorted")
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
                        mockMvcPostRequest("/contests/create", "ADMIN")
                            .content(createPayload),
                    ).andExpect(status().isOk)
                    .andReturn()

            val contestId = mapper.readTree(createResult.response.contentAsString)["contestId"].asLong()

            mockMvc
                .perform(
                    mockMvcPatchRequest("/contests/update", "ADMIN")
                        .content(
                            """
                            {
                              "contestId": $contestId,
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

        private fun createUser(): Long {
            val email = "leaderboard-${UUID.randomUUID()}@test.com"
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/users/create", "ADMIN")
                            .content("""{"email":"$email"}"""),
                    ).andExpect(status().isOk)
                    .andReturn()

            return mapper.readTree(result.response.contentAsString)["userId"].asLong()
        }
    }
