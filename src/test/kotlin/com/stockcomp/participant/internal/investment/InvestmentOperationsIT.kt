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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
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

        @Test
        fun `should not expose investments owned by another user`() {
            createUser(userEmail)
            createUser("other-investor@mail.com")
            val contest = createContest("OwnershipInvestContest")
            val participant = signUpForContest(contest.contestId)
            seedInvestment(participant.participantId, "AAPL")

            val allResult =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/all", emailClaim = "other-investor@mail.com")
                            .queryParam("contestId", contest.contestId.toString()),
                    ).andExpect(status().isOk)
                    .andReturn()

            val investments: List<InvestmentDto> = mapper.readValue(allResult.response.contentAsString)
            assertTrue(investments.isEmpty())

            mockMvc
                .perform(
                    mockMvcGetRequest(basePath, emailClaim = "other-investor@mail.com")
                        .queryParam("contestId", contest.contestId.toString())
                        .queryParam("symbol", "AAPL"),
                ).andExpect(status().isNotFound)
        }

        @Test
        fun `should remove investment when all units are sold`() {
            createUser(userEmail)
            val contest = createContest("SellAllContest")
            val participant = signUpForContest(contest.contestId)
            seedInvestment(participant.participantId, "MSFT", amount = 40)

            val persistedParticipant =
                participantRepository.findByParticipantId(participant.participantId)
                    ?: throw NoSuchElementException("Participant ${participant.participantId} was not found in test setup")
            persistedParticipant.updateParticipantWhenSelling(amount = 40, symbol = "MSFT", currentPrice = BigDecimal("125.0"))
            persistedParticipant.updateInvestmentValues()
            participantRepository.saveAndFlush(persistedParticipant)

            assertTrue(persistedParticipant.investments().isEmpty())
        }

        private fun seedInvestment(
            participantId: Long,
            symbol: String,
            amount: Int = 4,
        ) {
            val participant =
                participantRepository.findByParticipantId(participantId)
                    ?: throw NoSuchElementException("Participant $participantId was not found in test setup")
            participant.updateParticipantWhenBuying(amount = amount, symbol = symbol, currentPrice = BigDecimal("100.0"))
            participant.updateInvestmentValues()
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
