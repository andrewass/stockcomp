package com.stockcomp.controller

import com.stockcomp.IntegrationTest
import com.stockcomp.controller.common.createCookie
import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.user.User
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.service.security.DefaultJwtService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
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

    private val username = "testUser"
    private val password = "testPassword"
    private val email = "testEmail"
    private val contestNumber = "100"
    private val runningContest = true
    private val completedContest = true

    @Test
    fun `should sign up for running contest`() {
        createTestData(runningContest, !completedContest)
        val accessToken = jwtService.generateTokenPair(username).first

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("contestNumber", contestNumber)
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should return status 404 when signing up for completed contest`() {
        createTestData(!runningContest, completedContest)
        val accessToken = jwtService.generateTokenPair(username).first

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("contestNumber", contestNumber)
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should get list of upcoming contests`() {
        createTestData(runningContest, !completedContest)
        val accessToken = jwtService.generateTokenPair(username).first

        mockMvc.perform(
            MockMvcRequestBuilders.get("/contest/upcoming-contests")
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should get remaining funds for a contest participant`() {
        createTestData(runningContest, !completedContest)
        val accessToken = jwtService.generateTokenPair(username).first

        mockMvc.perform(
            MockMvcRequestBuilders.get("/contest/remaining-funds")
                .param("contestNumber", contestNumber)
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should return status 404 when fetching remaining funds for completed contest`() {
        createTestData(!runningContest, completedContest)
        val accessToken = jwtService.generateTokenPair(username).first

        mockMvc.perform(
            MockMvcRequestBuilders.get("/contest/remaining-funds")
                .param("contestNumber", contestNumber)
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
    }

    private fun createTestData(runningContest: Boolean, completedContest: Boolean) {
        val user = User(username = username, email = email, password = password)
        val contest = Contest(
            contestNumber = contestNumber.toInt(), startTime = LocalDateTime.now(),
            running = runningContest, completed = completedContest
        )
        val participant = Participant(user = user, contest = contest)
        userRepository.save(user)
        contestRepository.save(contest)
        participantRepository.save(participant)
    }
}