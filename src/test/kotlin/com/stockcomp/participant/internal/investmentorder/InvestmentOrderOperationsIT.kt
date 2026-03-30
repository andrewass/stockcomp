package com.stockcomp.participant.internal.investmentorder

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcDeleteRequest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.CreateContestRequest
import com.stockcomp.participant.InvestmentOrderDto
import com.stockcomp.participant.PlaceInvestmentOrderRequest
import com.stockcomp.participant.SignUpParticipantRequest
import com.stockcomp.participant.UserParticipantDto
import com.stockcomp.participant.internal.ParticipantRepository
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
class InvestmentOrderOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val participantRepository: ParticipantRepository,
    ) {
        private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        private val basePath = "/participants/investmentorders"
        private val contestStartTime = LocalDateTime.now()
        private val userEmail = "orders@mail.com"

        @Test
        fun `should place and list active investment orders`() {
            createUser(userEmail)
            val contest = createContest("OrdersContest")
            val participant = signUpForContest(contest.contestId)
            placeInvestmentOrder(participant.participantId)

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/all-active", emailClaim = userEmail)
                            .queryParam("contestId", contest.contestId.toString()),
                    ).andExpect(status().isOk)
                    .andReturn()

            val orders: List<InvestmentOrderDto> = mapper.readValue(result.response.contentAsString)
            assertTrue(orders.isNotEmpty())
            assertEquals("AAPL", orders.first().symbol)
        }

        @Test
        fun `should delete investment order`() {
            createUser(userEmail)
            val contest = createContest("DeleteOrdersContest")
            val participant = signUpForContest(contest.contestId)
            placeInvestmentOrder(participant.participantId)

            val persistedParticipant =
                participantRepository.findByParticipantId(participant.participantId)
                    ?: throw NoSuchElementException("Participant ${participant.participantId} missing in test setup")
            val orderId = persistedParticipant.investmentOrders().first().orderId!!

            mockMvc
                .perform(
                    mockMvcDeleteRequest("$basePath/delete", emailClaim = userEmail)
                        .queryParam("orderId", orderId.toString())
                        .queryParam("contestId", contest.contestId.toString()),
                ).andExpect(status().isOk)

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/all-active", emailClaim = userEmail)
                            .queryParam("contestId", contest.contestId.toString()),
                    ).andExpect(status().isOk)
                    .andReturn()

            val orders: List<InvestmentOrderDto> = mapper.readValue(result.response.contentAsString)
            assertTrue(orders.isEmpty())
        }

        @Test
        fun `should not place order for participant owned by another user`() {
            createUser(userEmail)
            createUser("other-user@mail.com")
            val contest = createContest("OwnershipContest")
            val participant = signUpForContest(contest.contestId)

            mockMvc
                .perform(
                    mockMvcPostRequest(url = "$basePath/order", emailClaim = "other-user@mail.com")
                        .content(
                            mapper.writeValueAsString(
                                PlaceInvestmentOrderRequest(
                                    participantId = participant.participantId,
                                    symbol = "AAPL",
                                    amount = 10,
                                    currency = "USD",
                                    expirationTime = LocalDateTime.now().plusDays(10),
                                    acceptedPrice = 100.0,
                                    transactionType = TransactionType.BUY,
                                ),
                            ),
                        ),
                ).andExpect(status().isNotFound)
        }

        private fun placeInvestmentOrder(participantId: Long) {
            mockMvc
                .perform(
                    mockMvcPostRequest(url = "$basePath/order", emailClaim = userEmail)
                        .content(
                            mapper.writeValueAsString(
                                PlaceInvestmentOrderRequest(
                                    participantId = participantId,
                                    symbol = "AAPL",
                                    amount = 10,
                                    currency = "USD",
                                    expirationTime = LocalDateTime.now().plusDays(10),
                                    acceptedPrice = 100.0,
                                    transactionType = TransactionType.BUY,
                                ),
                            ),
                        ),
                ).andExpect(status().isOk)
        }

        private fun signUpForContest(contestId: Long): UserParticipantDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest(url = "/participants/sign-up", emailClaim = userEmail)
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
                            .content(mapper.writeValueAsString(CreateContestRequest(contestName, contestStartTime, 30L))),
                    ).andExpect(status().isOk)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }

        private fun createUser(email: String): UserDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/users/create", "ADMIN")
                            .content(mapper.writeValueAsString(CreateUserRequest(email))),
                    ).andExpect(status().isOk)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }
    }
