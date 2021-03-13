package com.stockcomp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.IntegrationTest
import com.stockcomp.entity.User
import com.stockcomp.entity.contest.Contest
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.InvestmentTransactionRequest
import org.apache.kafka.clients.consumer.Consumer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.std.StringDeserializer
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

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var kafkaProperties: KafkaProperties

    private val username = "testUser"
    private val contestNumber = "100"
    private val buyInvestmentTopic = "buy-investment"

    @Test
    fun `should return status 200 when signing up for contest`(){
        createTestData()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("username", username)
                .param("contestNumber", contestNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should return status 404 when necessary objects not found`(){
        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/sign-up")
                .param("username", username)
                .param("contestNumber", contestNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should return status 200 when buying investment`(){

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contest/buy-investment")
                .content(createInvestmentTransactionRequest())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    private fun createInvestmentTransactionRequest(): String {
        val request = InvestmentTransactionRequest(
            username = "testUser",
            contestNumber = 150,
            investment = "Apple",
            amount = 100
        )

        return objectMapper.writeValueAsString(request)
    }


    private fun createTestData() {
        val contest = Contest(contestNumber = contestNumber.toInt(), startTime = LocalDateTime.now())
        val user = User(username = username, email = "test@mail.com", password = "testpassword")

        contestRepository.save(contest)
        userRepository.save(user)
    }
}