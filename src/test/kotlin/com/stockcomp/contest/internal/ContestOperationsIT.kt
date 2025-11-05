package com.stockcomp.contest.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcDeleteRequest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPatchRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestPageDto
import com.stockcomp.contest.CreateContestRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ControllerIntegrationTest
class ContestOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val objectMapper: ObjectMapper,
        private val contestService: ContestService,
    ) {
        private val basePath = "/contests"
        private val contestStartTime = LocalDateTime.now()

        @Test
        fun `should create a new contest with expected fields`() {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("$basePath/create", "ADMIN")
                            .content(
                                objectMapper.writeValueAsString(
                                    CreateContestRequest("TestContest", contestStartTime, 30L),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            val contest = objectMapper.readValue(result.response.contentAsString, ContestDto::class.java)
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

            val response = objectMapper.readValue(result.response.contentAsString, ExistsActiveContestResponse::class.java)
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

            val response = objectMapper.readValue(result.response.contentAsString, ContestsResponse::class.java)
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

            val contest = objectMapper.readValue(result.response.contentAsString, ContestDto::class.java)
            assertEquals(existingContest.contestId, contest.contestId)
        }

        @Test
        fun `should find all contests`() {
            val firstContest = contestService.createContest("TestContest", contestStartTime, 30L)
            val secondContest = contestService.createContest("AnotherTestContest", contestStartTime, 30L)

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/all")
                            .queryParam("pageNumber", "0")
                            .queryParam("pageSize", "5"),
                    ).andExpect(status().isOk)
                    .andReturn()

            val contestPage = objectMapper.readValue(result.response.contentAsString, ContestPageDto::class.java)
            assertTrue(contestPage.contests.size == 2)
            assertTrue(contestPage.contests.any { it.contestId == firstContest.contestId })
            assertTrue(contestPage.contests.any { it.contestId == secondContest.contestId })
        }

        @Test
        fun `should update a contest with expected value`() {
            val existingContest = contestService.createContest("TestContest", contestStartTime, 30L)

            val result =
                mockMvc
                    .perform(
                        mockMvcPatchRequest("$basePath/update", "ADMIN")
                            .content(
                                objectMapper.writeValueAsString(
                                    UpdateContestRequest(
                                        contestId = existingContest.contestId!!,
                                        contestStatus = ContestStatus.RUNNING,
                                    ),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            val contest = objectMapper.readValue(result.response.contentAsString, ContestDto::class.java)
            assertEquals(existingContest.contestId, contest.contestId)
            assertEquals(ContestStatus.RUNNING, contest.contestStatus)
        }

        @Test
        fun `should delete an existing contest`() {
            val existingContest = contestService.createContest("TestContest", contestStartTime, 30L)

            mockMvc
                .perform(mockMvcDeleteRequest("$basePath/${existingContest.contestId}", "ADMIN"))
                .andExpect(status().isOk)

            val persistedContests = contestService.getAllContestsSorted(0, 5)
            assertTrue(persistedContests.totalElements == 0L)
        }
    }
