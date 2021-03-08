package com.stockcomp.controller

import com.stockcomp.IntegrationTest
import com.stockcomp.entity.User
import com.stockcomp.entity.contest.Contest
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime
import javax.transaction.Transactional

@Transactional
@AutoConfigureMockMvc
internal class ContestControllerIT : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var contestRepository: ContestRepository

    val username = "testUser"
    val contestNumber = "100"

    @Test
    fun shouldSignUpForContest() {
        createTestData()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("username", username)
                .param("contestNumber", contestNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    private fun createTestData() {
        val contest = Contest(contestNumber = contestNumber.toInt(), startTime = LocalDateTime.now())
        val user = User(username = username, email = "test@mail.com", password = "testpassword")

        contestRepository.save(contest)
        userRepository.save(user)
    }
}