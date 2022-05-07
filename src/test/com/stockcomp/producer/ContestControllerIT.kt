package com.stockcomp.producer

import com.stockcomp.IntegrationTest
import com.stockcomp.producer.common.createCookie
import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.domain.user.User
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.service.security.DefaultJwtService
import org.junit.jupiter.api.Disabled
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
    private val country = "Canada"
    private val contestNumber = "100"


    @Disabled
    @Test
    fun `should return status 404 when fetching remaining funds for completed contest`() {
        createTestData(ContestStatus.COMPLETED)
        val accessToken = jwtService.generateTokenPair(username).first

        mockMvc.perform(
            MockMvcRequestBuilders.get("/contest/remaining-funds")
                .param("contestNumber", contestNumber)
                .cookie(createCookie("accessToken", accessToken, 1000))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
    }

    private fun createTestData(contestStatus : ContestStatus) {
        val user = User(username = username, email = email, password = password, country = country)
        val contest = Contest(
            contestNumber = contestNumber.toInt(), startTime = LocalDateTime.now(),
            endTime =  LocalDateTime.now().plusMonths(2), contestStatus = contestStatus
        )
        val participant = Participant(user = user, contest = contest, rank = 1)
        userRepository.save(user)
        contestRepository.save(contest)
        participantRepository.save(participant)
    }
}