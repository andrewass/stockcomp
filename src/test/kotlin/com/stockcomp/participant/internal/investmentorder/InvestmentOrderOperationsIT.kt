package com.stockcomp.participant.internal.investmentorder

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcDeleteRequest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPatchRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.CreateContestRequest
import com.stockcomp.participant.InvestmentOrderDto
import com.stockcomp.participant.PlaceInvestmentOrderRequest
import com.stockcomp.participant.SignUpParticipantRequest
import com.stockcomp.participant.TransactionType
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
import java.math.BigDecimal
import java.time.LocalDateTime

@ControllerIntegrationTest
class InvestmentOrderOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val participantRepository: ParticipantRepository,
    ) {
        private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        private val basePath = "/participants/investment-orders"
        private val contestStartTime = LocalDateTime.now()
        private val userEmail = "orders@mail.com"

        @Test
        fun `should place and list active investment orders`() {
            createUser(userEmail)
            val contest = createContest("OrdersContest")
            val participant = signUpForContest(contest.contestId)
            updateContestStatus(contest.contestId, "RUNNING")
            placeInvestmentOrder(participant.participantId)

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/active", emailClaim = userEmail)
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
            updateContestStatus(contest.contestId, "RUNNING")
            placeInvestmentOrder(participant.participantId)

            val persistedParticipant =
                participantRepository.findByParticipantId(participant.participantId)
                    ?: throw NoSuchElementException("Participant ${participant.participantId} missing in test setup")
            val orderId = persistedParticipant.investmentOrders().first().orderId!!

            mockMvc
                .perform(
                    mockMvcDeleteRequest("$basePath/$orderId", emailClaim = userEmail)
                        .queryParam("contestId", contest.contestId.toString()),
                ).andExpect(status().isNoContent)

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/active", emailClaim = userEmail)
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
                    mockMvcPostRequest(url = basePath, emailClaim = "other-user@mail.com")
                        .content(
                            mapper.writeValueAsString(
                                PlaceInvestmentOrderRequest(
                                    participantId = participant.participantId,
                                    symbol = "AAPL",
                                    amount = 10,
                                    currency = "USD",
                                    expirationTime = LocalDateTime.now().plusDays(10),
                                    acceptedPrice = BigDecimal("100.0"),
                                    transactionType = TransactionType.BUY,
                                ),
                            ),
                        ),
                ).andExpect(status().isNotFound)
        }

        @Test
        fun `should pause orders without cancelling them and resume trading when contest restarts`() {
            createUser(userEmail)
            val contest = createContest("PausedOrdersContest")
            val participant = signUpForContest(contest.contestId)
            updateContestStatus(contest.contestId, "RUNNING")
            placeInvestmentOrder(participant.participantId)

            updateContestStatus(contest.contestId, "STOPPED")
            placeInvestmentOrder(participant.participantId, status().isConflict)

            val pausedOrders = getActiveOrders(contest.contestId)
            assertEquals(1, pausedOrders.size)

            updateContestStatus(contest.contestId, "RUNNING")
            placeInvestmentOrder(participant.participantId)

            assertEquals(2, getActiveOrders(contest.contestId).size)
        }

        private fun placeInvestmentOrder(
            participantId: Long,
            expectedStatus: org.springframework.test.web.servlet.ResultMatcher = status().isCreated,
        ) {
            mockMvc
                .perform(
                    mockMvcPostRequest(url = basePath, emailClaim = userEmail)
                        .content(
                            mapper.writeValueAsString(
                                PlaceInvestmentOrderRequest(
                                    participantId = participantId,
                                    symbol = "AAPL",
                                    amount = 10,
                                    currency = "USD",
                                    expirationTime = LocalDateTime.now().plusDays(10),
                                    acceptedPrice = BigDecimal("100.0"),
                                    transactionType = TransactionType.BUY,
                                ),
                            ),
                        ),
                ).andExpect(expectedStatus)
        }

        private fun getActiveOrders(contestId: Long): List<InvestmentOrderDto> {
            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/active", emailClaim = userEmail)
                            .queryParam("contestId", contestId.toString()),
                    ).andExpect(status().isOk)
                    .andReturn()
            return mapper.readValue(result.response.contentAsString)
        }

        private fun signUpForContest(contestId: Long): UserParticipantDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest(url = "/participants", emailClaim = userEmail)
                            .content(mapper.writeValueAsString(SignUpParticipantRequest(contestId))),
                    ).andExpect(status().isCreated)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }

        private fun createContest(contestName: String): ContestDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/contests", "ADMIN")
                            .content(mapper.writeValueAsString(CreateContestRequest(contestName, contestStartTime, 30L))),
                    ).andExpect(status().isCreated)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }

        private fun updateContestStatus(
            contestId: Long,
            contestStatus: String,
        ) {
            mockMvc
                .perform(
                    mockMvcPatchRequest("/contests/$contestId", "ADMIN")
                        .content(mapper.writeValueAsString(mapOf("contestStatus" to contestStatus))),
                ).andExpect(status().isOk)
        }

        private fun createUser(email: String): UserDto {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/users", "ADMIN")
                            .content(mapper.writeValueAsString(CreateUserRequest(email))),
                    ).andExpect(status().isCreated)
                    .andReturn()

            return mapper.readValue(result.response.contentAsString)
        }
    }
