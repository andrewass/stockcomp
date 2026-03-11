package com.stockcomp.participant.internal.investment

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.CreateContestRequest
import com.stockcomp.participant.InvestmentDto
import com.stockcomp.participant.SignUpParticipantRequest
import com.stockcomp.participant.UserParticipantDto
import com.stockcomp.participant.internal.ParticipantRepository
import com.stockcomp.user.CreateUserRequest
import com.stockcomp.user.UserDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ControllerIntegrationTest
class InvestmentOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val participantRepository: ParticipantRepository,
    ) {
        private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        private val basePath = "/participants/investments"
        private val contestStartTime = LocalDateTime.now()
        private val userEmail = "investor@mail.com"

        @Test
        fun `should return investments for participant`() {
            createUser(userEmail)
            val contest = createContest("InvestContest")
            val participant = signUpForContest(contest.contestId)
            seedInvestment(participant.participantId, "AAPL")

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/all", emailClaim = userEmail)
                            .queryParam("contestId", contest.contestId.toString()),
                    ).andExpect(status().isOk)
                    .andReturn()

            val investments: List<InvestmentDto> = mapper.readValue(result.response.contentAsString)
            assertEquals(1, investments.size)
            assertEquals("AAPL", investments.first().symbol)
        }

        @Test
        fun `should return investment for symbol`() {
            createUser(userEmail)
            val contest = createContest("SymbolContest")
            val participant = signUpForContest(contest.contestId)
            seedInvestment(participant.participantId, "MSFT")

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest(basePath, emailClaim = userEmail)
                            .queryParam("contestId", contest.contestId.toString())
                            .queryParam("symbol", "MSFT"),
                    ).andExpect(status().isOk)
                    .andReturn()

            val investment: InvestmentDto = mapper.readValue(result.response.contentAsString)
            assertEquals("MSFT", investment.symbol)
        }

        private fun seedInvestment(
            participantId: Long,
            symbol: String,
        ) {
            val participant = participantRepository.findByParticipantId(participantId)
            val investment =
                Investment(
                    symbol = symbol,
                    participant = participant,
                    amount = 4,
                    averageUnitCost = 100.0,
                    totalProfit = 5.0,
                    totalValue = 400.0,
                )
            participant.investments.add(investment)
            participantRepository.save(participant)
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
