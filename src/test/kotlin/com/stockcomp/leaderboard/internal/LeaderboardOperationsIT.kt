package com.stockcomp.leaderboard.internal

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.leaderboard.LeaderboardEntryDto
import com.stockcomp.leaderboard.LeaderboardEntryPageDto
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerIntegrationTest
class LeaderboardOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val leaderboardRepository: LeaderboardRepository,
        private val leaderboardEntryRepository: LeaderboardEntryRepository,
    ) {
        private val mapper = jacksonObjectMapper()
        private val basePath = "/leaderboard"

        @Test
        fun `should return sorted leaderboard entries`() {
            val leaderboard = leaderboardRepository.save(Leaderboard(leaderboardId = 1L))
            leaderboardEntryRepository.save(LeaderboardEntry(leaderboard = leaderboard, userId = 12L))

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
            val leaderboard = leaderboardRepository.save(Leaderboard(leaderboardId = 2L))
            leaderboardEntryRepository.save(LeaderboardEntry(leaderboard = leaderboard, userId = 99L))

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/user/99"))
                    .andExpect(status().isOk)
                    .andReturn()

            val response: LeaderboardEntryDto = mapper.readValue(result.response.contentAsString)
            assertEquals(0, response.score)
        }

        @Test
        fun `should update leaderboard`() {
            mockMvc
                .perform(
                    mockMvcPostRequest("$basePath/update")
                        .queryParam("contestId", "1"),
                ).andExpect(status().isOk)
        }
    }
