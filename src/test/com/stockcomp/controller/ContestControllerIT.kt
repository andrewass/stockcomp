package com.stockcomp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.IntegrationTest
import com.stockcomp.controller.common.generateToken
import com.stockcomp.entity.User
import com.stockcomp.entity.contest.Contest
import com.stockcomp.entity.contest.Investment
import com.stockcomp.entity.contest.Participant
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.repository.jpa.UserRepository
import com.stockcomp.request.InvestmentTransactionRequest
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
import java.time.LocalDateTime
import javax.servlet.http.Cookie

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
    lateinit var objectMapper: ObjectMapper

    private val username = "testUser"
    private val password = "testPassword"
    private val email = "testEmail"
    private val contestNumber = "100"
    private val symbol = "AAPL"
    private val description = "APPLE INC"
    private val jwt = generateToken(username)

    @Test
    fun `should return status 200 when signing up for contest`() {
        createTestData()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("username", username)
                .param("contestNumber", contestNumber)
                .cookie(Cookie("jwt", jwt))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should return status 404 when necessary objects not found on sign up`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("username", username)
                .param("contestNumber", contestNumber)
                .cookie(Cookie("jwt", jwt))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should return status ok when buying investment`() {
        val (user, contest) = createTestData()
        contest.startContest()
        signUpUserForContest(user, contest)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/buy-investment")
                .content(createInvestmentTransactionRequest())
                .cookie(Cookie("jwt", jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("symbol").value(symbol))
            .andExpect(jsonPath("amount").value("100"))
    }

    @Test
    fun `should return status ok when selling investment`() {
        val (user, contest) = createTestData()
        contest.startContest()
        signUpUserForContest(user, contest)
        buyInvestment(contest)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sell-investment")
                .content(createInvestmentTransactionRequest())
                .cookie(Cookie("jwt", jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("symbol").value(symbol))
            .andExpect(jsonPath("amount").value("100"))
    }

    @Test
    fun `should get investments of a symbol for a given participant`() {
        val (user, contest) = createTestData()
        contest.startContest()
        signUpUserForContest(user, contest)
        buyInvestment(contest)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/contest/symbol-investment")
                .param("symbol", symbol)
                .param("contestNumber", contestNumber)
                .cookie(Cookie("jwt", jwt))
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
            expirationTime = LocalDateTime.now()
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