package com.stockcomp.participant.internal

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.CreateContestRequest
import com.stockcomp.participant.ContestParticipantDto
import com.stockcomp.participant.DetailedParticipantDto
import com.stockcomp.participant.PlaceInvestmentOrderRequest
import com.stockcomp.participant.SignUpParticipantRequest
import com.stockcomp.participant.UserParticipantDto
import com.stockcomp.user.CreateUserRequest
import com.stockcomp.user.UserDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ControllerIntegrationTest
class ParticipantOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val participantService: ParticipantService,
    ) {
        private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

        private val basePath = "/participants"
        private val contestStartTime = LocalDateTime.now()
        private val userEmail = "test@mail.com"

        @Test
        fun `should sign up user as a participant for a given contest`() {
            val user = createUser(userEmail)
            val contest = createContest("firstContest")

            val participant = signUpForContest(contest.contestId)

            assertEquals(user.userId, participant.userId)
        }

        @Test
        fun `should return all participations for a given user`() {
            val user = createUser(userEmail)
            val contest = createContest("firstContest")
            signUpForContest(contest.contestId)

            val result =
                mockMvc
                    .perform(mockMvcGetRequest(url = "$basePath/registered", emailClaim = userEmail))
                    .andExpect(status().isOk)
                    .andReturn()

            val participants: List<ContestParticipantDto> = mapper.readValue(result.response.contentAsString)
            assertTrue(participants.size == 1)
            assertEquals(user.userId, participants.first().participant.userId)
            assertEquals(contest.contestId, participants.first().contest.contestId)
        }

        @Test
        fun `should return all contests where user is not participating`() {
            createUser(userEmail)
            val contest = createContest("firstContest")

            val result =
                mockMvc
                    .perform(mockMvcGetRequest(url = "$basePath/unregistered", emailClaim = userEmail))
                    .andExpect(status().isOk)
                    .andReturn()

            val contests: List<ContestDto> = mapper.readValue(result.response.contentAsString)
            assertTrue(contests.size == 1)
            assertEquals(contest.contestId, contests.first().contestId)
        }

        @Test
        fun `should return all participants where there exists investmentsorders of a given symbol`() {
            createUser(userEmail)
            val contest = createContest("firstContest")
            val participant = signUpForContest(contest.contestId)
            placeInvestmentOrder(participant)

            val result =
                mockMvc
                    .perform(mockMvcGetRequest(url = "$basePath/detailed/symbol/AAPL", emailClaim = userEmail))
                    .andExpect(status().isOk)
                    .andReturn()

            val detailedParticipants: List<DetailedParticipantDto> = mapper.readValue(result.response.contentAsString)
            assertTrue(detailedParticipants.size == 1)
        }

        private fun placeInvestmentOrder(participant: UserParticipantDto) {
            mockMvc
                .perform(
                    mockMvcPostRequest(url = "$basePath/investmentorders/order", emailClaim = userEmail)
                        .content(
                            mapper.writeValueAsString(
                                PlaceInvestmentOrderRequest(
                                    participantId = participant.participantId,
                                    symbol = "AAPL",
                                    amount = 100,
                                    currency = "USD",
                                    expirationTime = LocalDateTime.now().plusDays(10),
                                    acceptedPrice = 100.00,
                                    transactionType = "BUY",
                                ),
                            ),
                        ),
                ).andExpect(status().isOk)
        }

        private fun signUpForContest(contestId: Long): UserParticipantDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest(url = "$basePath/sign-up", emailClaim = userEmail)
                            .content(mapper.writeValueAsString(SignUpParticipantRequest(contestId))),
                    ).andExpect(status().isOk)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }

        private fun createContest(contestName: String): ContestDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/contests/create", "ADMIN")
                            .content(
                                mapper.writeValueAsString(
                                    CreateContestRequest(contestName, contestStartTime, 30L),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }

        private fun createUser(email: String): UserDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/users/create", "ADMIN")
                            .content(
                                mapper.writeValueAsString(
                                    CreateUserRequest(email),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }
    }
