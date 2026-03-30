package com.stockcomp.contest.internal

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcDeleteRequest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPatchRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestPageDto
import com.stockcomp.contest.CreateContestRequest
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

@ControllerIntegrationTest
class ContestOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val contestService: ContestService,
        private val contestOperationService: ContestOperationService,
    ) {
        @MockkBean
        private lateinit var clock: Clock

        private val basePath = "/contests"
        private val contestStartTime = LocalDateTime.now()
        private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

        @Test
        fun `should create a new contest with expected fields`() {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("$basePath/create", "ADMIN")
                            .content(
                                mapper.writeValueAsString(
                                    CreateContestRequest("TestContest", contestStartTime, 30L),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            val contest = mapper.readValue(result.response.contentAsString, ContestDto::class.java)
            assertEquals("TestContest", contest.contestName)
            assertEquals(contestStartTime, contest.startTime)
            assertEquals(contestStartTime.plusDays(30), contest.endTime)
            assertEquals(ContestStatus.AWAITING_START, contest.contestStatus)
        }

        @Test
        fun `should exist an active contest`() {
            contestService.createContest("TestContest", contestStartTime, 30L)

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/exists-active"))
                    .andExpect(status().isOk)
                    .andReturn()

            val response = mapper.readValue(result.response.contentAsString, ExistsActiveContestResponse::class.java)
            assertTrue(response.existsActiveContests)
        }

        @Test
        fun `should find an active contest`() {
            val existingContest = contestService.createContest("TestContest", contestStartTime, 30L)

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/active"))
                    .andExpect(status().isOk)
                    .andReturn()

            val response = mapper.readValue(result.response.contentAsString, ContestsResponse::class.java)
            assertTrue(response.contests.size == 1)
            assertEquals(existingContest.contestId, response.contests.first().contestId)
        }

        @Test
        fun `should find contest by id`() {
            val existingContest = contestService.createContest("TestContest", contestStartTime, 30L)

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/${existingContest.contestId}"))
                    .andExpect(status().isOk)
                    .andReturn()

            val contest = mapper.readValue(result.response.contentAsString, ContestDto::class.java)
            assertEquals(existingContest.contestId, contest.contestId)
        }

        @Test
        fun `should return not found for unknown contest id`() {
            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/99999999"))
                    .andExpect(status().isNotFound)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            assertEquals(404, response["status"].asInt())
            assertEquals("Contest not found", response["title"].asText())
            assertEquals("/problems/contest/not-found", response["type"].asText())
            assertEquals("$basePath/99999999", response["instance"].asText())
        }

        @Test
        fun `should find all contests`() {
            val firstContest = contestService.createContest("TestContest", contestStartTime, 30L)
            val secondContest = contestService.createContest("AnotherTestContest", contestStartTime, 30L)
            val firstContestId = firstContest.contestId!!
            val secondContestId = secondContest.contestId!!
            val pageSize = 5
            val firstPage = fetchContestsPage(pageNumber = 0, pageSize = pageSize)
            val totalPages =
                if (firstPage.totalEntriesCount == 0L) {
                    0
                } else {
                    ((firstPage.totalEntriesCount - 1) / pageSize + 1).toInt()
                }
            val foundContestIds = firstPage.contests.map { it.contestId }.toMutableSet()

            for (pageNumber in 1 until totalPages) {
                if (foundContestIds.contains(firstContestId) && foundContestIds.contains(secondContestId)) {
                    break
                }

                val contestPage = fetchContestsPage(pageNumber = pageNumber, pageSize = pageSize)
                foundContestIds.addAll(contestPage.contests.map { it.contestId })
            }

            assertTrue(foundContestIds.contains(firstContestId))
            assertTrue(foundContestIds.contains(secondContestId))
        }

        @Test
        fun `should update a contest with expected value`() {
            val existingContest = contestService.createContest("TestContest", contestStartTime, 30L)

            val result =
                mockMvc
                    .perform(
                        mockMvcPatchRequest("$basePath/update", "ADMIN")
                            .content(
                                mapper.writeValueAsString(
                                    UpdateContestRequest(
                                        contestId = existingContest.contestId!!,
                                        contestStatus = ContestStatus.RUNNING,
                                    ),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            val contest = mapper.readValue(result.response.contentAsString, ContestDto::class.java)
            assertEquals(existingContest.contestId, contest.contestId)
            assertEquals(ContestStatus.RUNNING, contest.contestStatus)
        }

        @Test
        fun `should update start time and preserve contest duration before contest starts`() {
            val initialStart = LocalDateTime.now().plusDays(7)
            val durationDays = 12L
            val contest = contestService.createContest("FutureContest", initialStart, durationDays)
            val updatedStart = initialStart.plusDays(5)

            val result =
                mockMvc
                    .perform(
                        mockMvcPatchRequest("$basePath/update", "ADMIN")
                            .content(
                                mapper.writeValueAsString(
                                    UpdateContestRequest(
                                        contestId = contest.contestId!!,
                                        startTime = updatedStart,
                                    ),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            val updatedContest = mapper.readValue(result.response.contentAsString, ContestDto::class.java)
            assertEquals(updatedStart, updatedContest.startTime)
            assertEquals(updatedStart.plusDays(durationDays), updatedContest.endTime)
        }

        @Test
        fun `should reject start time update when contest status is not awaiting start`() {
            val contest = contestService.createContest("StartedContest", LocalDateTime.now().plusDays(5), 10L)
            val contestId = contest.contestId!!
            contestService.updateContest(
                contestId = contestId,
                contestName = null,
                contestStatus = ContestStatus.RUNNING,
                startTime = null,
            )

            val result =
                mockMvc
                    .perform(
                        mockMvcPatchRequest("$basePath/update", "ADMIN")
                            .content(
                                mapper.writeValueAsString(
                                    UpdateContestRequest(
                                        contestId = contestId,
                                        startTime = LocalDateTime.now().plusDays(2),
                                    ),
                                ),
                            ),
                    ).andExpect(status().isConflict)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            assertEquals(409, response["status"].asInt())
            assertEquals("Contest state conflict", response["title"].asText())
            assertEquals("/problems/contest/state-conflict", response["type"].asText())
        }

        @Test
        fun `should delete an existing contest`() {
            val existingContest = contestService.createContest("TestContest", contestStartTime, 30L)
            val contestId = existingContest.contestId!!

            mockMvc
                .perform(mockMvcDeleteRequest("$basePath/$contestId", "ADMIN"))
                .andExpect(status().isNoContent)

            mockMvc
                .perform(mockMvcGetRequest("$basePath/$contestId"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should return not found when deleting unknown contest`() {
            mockMvc
                .perform(mockMvcDeleteRequest("$basePath/99999999", "ADMIN"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should return forbidden when non-admin tries to create contest`() {
            mockMvc
                .perform(
                    mockMvcPostRequest("$basePath/create", "USER")
                        .content(
                            mapper.writeValueAsString(
                                CreateContestRequest("ForbiddenContest", contestStartTime, 5L),
                            ),
                        ),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return forbidden when non-admin tries to update contest`() {
            val contest = contestService.createContest("UpdatableContest", contestStartTime.plusDays(2), 10L)

            mockMvc
                .perform(
                    mockMvcPatchRequest("$basePath/update", "USER")
                        .content(
                            mapper.writeValueAsString(
                                UpdateContestRequest(
                                    contestId = contest.contestId!!,
                                    contestName = "RenamedByUser",
                                ),
                            ),
                        ),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return forbidden when non-admin tries to delete contest`() {
            val contest = contestService.createContest("DeletableContest", contestStartTime.plusDays(2), 10L)

            mockMvc
                .perform(mockMvcDeleteRequest("$basePath/${contest.contestId}", "USER"))
                .andExpect(status().isForbidden)
        }

        @Test
        fun `should return bad request for invalid contest create payload`() {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("$basePath/create", "ADMIN")
                            .content(
                                mapper.writeValueAsString(
                                    CreateContestRequest(
                                        contestName = "   ",
                                        startTime = contestStartTime,
                                        durationDays = 0L,
                                    ),
                                ),
                            ),
                    ).andExpect(status().isBadRequest)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            assertEquals(400, response["status"].asInt())
            assertEquals("Invalid contest request", response["title"].asText())
            assertEquals("/problems/contest/validation", response["type"].asText())
            assertTrue(response["errors"].isArray)
        }

        @Test
        fun `should return bad request for invalid contest update payload`() {
            mockMvc
                .perform(
                    mockMvcPatchRequest("$basePath/update", "ADMIN")
                        .content(
                            mapper.writeValueAsString(
                                UpdateContestRequest(
                                    contestId = 0L,
                                    contestName = "   ",
                                ),
                            ),
                        ),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should set contest to running when maintain status runs past start time`() {
            val now = LocalDateTime.of(2030, 1, 10, 12, 0)
            val contest = contestService.createContest("AwaitingContest", now.minusHours(1), 2L)

            stubClock(now)
            contestOperationService.maintainContestStatus()

            val persistedContest = contestService.getContest(contest.contestId!!)
            assertEquals(ContestStatus.RUNNING, persistedContest.contestStatus)
        }

        @Test
        fun `should set contest to awaiting completion when maintain status runs past end time`() {
            val now = LocalDateTime.of(2030, 1, 10, 12, 0)
            val contest = contestService.createContest("RunningContest", now.minusDays(2), 1L)
            val contestId = contest.contestId!!
            contestService.updateContest(
                contestId = contestId,
                contestName = null,
                contestStatus = ContestStatus.RUNNING,
                startTime = null,
            )

            stubClock(now)
            contestOperationService.maintainContestStatus()

            val persistedContest = contestService.getContest(contestId)
            assertEquals(ContestStatus.AWAITING_COMPLETION, persistedContest.contestStatus)
        }

        private fun stubClock(now: LocalDateTime) {
            every { clock.instant() } returns now.toInstant(ZoneOffset.UTC)
            every { clock.zone } returns ZoneOffset.UTC
        }

        private fun fetchContestsPage(
            pageNumber: Int,
            pageSize: Int,
        ): ContestPageDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/all")
                            .queryParam("pageNumber", pageNumber.toString())
                            .queryParam("pageSize", pageSize.toString()),
                    ).andExpect(status().isOk)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString, ContestPageDto::class.java)
        }
    }
