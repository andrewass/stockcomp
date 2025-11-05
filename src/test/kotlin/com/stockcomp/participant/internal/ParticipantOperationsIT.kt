package com.stockcomp.participant.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.contest.CreateContestRequest
import com.stockcomp.participant.SignUpParticipantRequest
import com.stockcomp.user.CreateUserRequest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@Disabled
@ControllerIntegrationTest
class ParticipantOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val objectMapper: ObjectMapper,
        private val contestService: ContestServiceExternal,
    ) {
        private val basePath = "/participants"
        private val contestStartTime = LocalDateTime.now()
        private val userEmail = "test@mail.com"

        @Test
        fun `should sign up user as a participant for a given contest`() {
            createUser(userEmail)
            createContest("testContest")
            val contestId = contestService.getActiveContests().first().contestId

            mockMvc
                .perform(
                    mockMvcPostRequest(url = "$basePath/sign-up", emailClaim = userEmail)
                        .content(objectMapper.writeValueAsString(SignUpParticipantRequest(contestId))),
                ).andExpect(status().isOk)
        }

        @Test
        fun `should get participant for given contest`() {
            mockMvc
                .perform(
                    mockMvcGetRequest("$basePath/contest")
                        .queryParam("contestId", "1"),
                ).andExpect(status().isOk)
        }

        @Test
        fun `should get running participants`() {
            mockMvc
                .perform(
                    mockMvcGetRequest("$basePath/running-participants")
                        .queryParam("symbol", "AAPL"),
                ).andExpect(status().isOk)
        }

        @Test
        fun `should get sorted participants`() {
            mockMvc
                .perform(
                    mockMvcGetRequest("$basePath/sorted")
                        .queryParam("contestId", "1")
                        .queryParam("pageNumber", "1")
                        .queryParam("pageSize", "1"),
                ).andExpect(status().isOk)
        }

        @Test
        fun `should get participant history for user`() {
            mockMvc
                .perform(
                    mockMvcGetRequest("$basePath/history")
                        .queryParam("username", "testUser"),
                ).andExpect(status().isOk)
        }

        private fun createContest(contestName: String) {
            mockMvc
                .perform(
                    mockMvcPostRequest("/contests/create", "ADMIN")
                        .content(
                            objectMapper.writeValueAsString(
                                CreateContestRequest(contestName, contestStartTime, 30L),
                            ),
                        ),
                )
        }

        private fun createUser(email: String) {
            mockMvc
                .perform(
                    mockMvcPostRequest("/users/create", "ADMIN")
                        .content(
                            objectMapper.writeValueAsString(
                                CreateUserRequest(email),
                            ),
                        ),
                )
        }
    }
