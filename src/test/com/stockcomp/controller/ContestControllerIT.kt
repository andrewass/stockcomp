package com.stockcomp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.IntegrationTest
import com.stockcomp.controller.common.createCookie
import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.user.User
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.repository.jpa.UserRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.service.security.DefaultJwtService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@WithMockUser
@Transactional
@AutoConfigureMockMvc
internal class ContestControllerIT : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var contestRepository: ContestRepository

    @Autowired
    lateinit var participantRepository: ParticipantRepository

    @Autowired
    lateinit var jwtService: DefaultJwtService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val username = "testUser"
    private val password = "testPassword"
    private val email = "testEmail"
    private val contestNumber = "100"
    private val symbol = "AAPL"
    private val description = "APPLE INC"

    @Test
    fun `should return status 200 when signing up for contest`() {
        createTestData()
        val accessToken = jwtService.generateTokenPair(username).first

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("username", username)
                .param("contestNumber", contestNumber)
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should return status ok when placing buy order`() {
        val (user, contest) = createTestData()
        val accessToken = jwtService.generateTokenPair(username).first
        contest.startContest()
        signUpUserForContest(user, contest)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/place-buy-order")
                .content(createInvestmentTransactionRequest())
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should return status ok when placing sell order`() {
        val (user, contest) = createTestData()
        val accessToken = jwtService.generateTokenPair(username).first
        contest.startContest()
        signUpUserForContest(user, contest)
        buyInvestment(contest)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/place-sell-order")
                .content(createInvestmentTransactionRequest())
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should get investments of a symbol for a given participant`() {
        val (user, contest) = createTestData()
        val accessToken = jwtService.generateTokenPair(username).first
        contest.startContest()
        signUpUserForContest(user, contest)
        buyInvestment(contest)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/contest/symbol-investment")
                .param("symbol", symbol)
                .param("contestNumber", contestNumber)
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("symbol").value(symbol))
            .andExpect(jsonPath("amount").value("100"))
    }

    private fun buyInvestment(contest: Contest) {
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest)
        val investment = Investment(
            name = description, symbol = symbol,
            portfolio = participant[0].portfolio, amount = 100
        )
        participant[0].portfolio.investments.add(investment)
        participantRepository.save(participant[0])
    }

    private fun createInvestmentTransactionRequest(): String {
        val request = InvestmentTransactionRequest(
            contestNumber = contestNumber.toInt(),
            symbol = symbol,
            amount = 100,
            acceptedPrice = 100.00,
            expirationTime = LocalDate.now()
        )
        return objectMapper.writeValueAsString(request)
    }

    private fun createTestData(): Pair<User, Contest> {
        val contest = Contest(contestNumber = contestNumber.toInt(), startTime = LocalDateTime.now())
        val user = User(username = username, email = email, password = password)
        contestRepository.save(contest)
        userRepository.save(user)

        return Pair(user, contest)
    }

    private fun signUpUserForContest(user: User, contest: Contest) {
        val participant = Participant(user = user, contest = contest)
        contest.participants.add(participant)
        contestRepository.save(contest)
    }
}