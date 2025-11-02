package com.stockcomp.contest.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.contest.ContestDto
import com.stockcomp.user.internal.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ControllerIntegrationTest
@AutoConfigureMockMvc
@Transactional
class ContestControllerIT @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
) {

    private val basePath = "/contests"
    private val contestStartTime = LocalDateTime.now()

    @Test
    fun `should create a new contest`() {
        val result = mockMvc.perform(
            mockMvcPostRequest("$basePath/create", UserRole.ADMIN.name)
                .content(
                    objectMapper.writeValueAsString(
                        CreateContestRequest(
                            contestName = "TestContest",
                            startTime = contestStartTime,
                            durationDays = 30L
                        )
                    )
                )
        ).andExpect(status().isOk)
            .andReturn()

        val contest = objectMapper.readValue(result.response.contentAsString, ContestDto::class.java)
        assertEquals("TestContest", contest.contestName)
        assertEquals(contestStartTime, contest.startTime)
        assertEquals(contestStartTime.plusDays(30),contest.endTime)
        assertEquals(ContestStatus.AWAITING_START, contest.contestStatus)
    }

    data class CreateContestRequest(
        val contestName: String,
        val startTime: LocalDateTime,
        val durationDays: Long,
    )
}
