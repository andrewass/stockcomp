package com.stockcomp.participant

import com.ninjasquad.springmockk.MockkBean
import com.stockcomp.configuration.SecurityConfiguration
import com.stockcomp.contest.domain.Contest
import com.stockcomp.participant.dto.DetailedParticipantDto
import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.entity.User
import com.stockcomp.util.mockMvcGetRequest
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@Import(SecurityConfiguration::class)
@WebMvcTest(ParticipantController::class)
class ParticipantControllerTest(
    @Autowired val mockMvc: MockMvc
) {
    private val identifier = "user"
    private val basePath = "/participants"

    @MockkBean
    lateinit var participantService: ParticipantService

    @Test
    fun `should get participant for given contest`() {
        every { participantService.getParticipant(1, identifier) }
            .returns(getParticipant())
        mockMvc.perform(
            mockMvcGetRequest("$basePath/contest")
                .queryParam("contestNumber", "1")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should get active participants`() {
        every { participantService.getActiveParticipants(identifier) }.returns(listOf(getParticipant()))
        mockMvc.perform(
            mockMvcGetRequest("$basePath/active")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should get running participants`() {
        every { participantService.getRunningDetailedParticipantsForSymbol(identifier, "AAPL") }
            .returns(listOf(getDetailedParticipant()))
        mockMvc.perform(
            mockMvcGetRequest("$basePath/running-participants")
                .queryParam("symbol", "AAPL")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should get sorted participants`() {
        every { participantService.getParticipantsSortedByRank(1,1,1) }
            .returns(PageImpl(listOf(getParticipant())))
        mockMvc.perform(
            mockMvcGetRequest("$basePath/sorted")
                .queryParam("contestNumber", "1")
                .queryParam("pageNumber", "1")
                .queryParam("pageSize", "1")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should get participant history for user`() {
        every { participantService.getParticipantHistory("testUser") }
            .returns(listOf(getParticipant()))
        mockMvc.perform(
            mockMvcGetRequest("$basePath/history")
                .queryParam("username", "testUser")
        ).andExpect(status().isOk)
    }

    private fun getParticipant() =
        Participant(
            contest = getContest(),
            rank = 1,
            user = getUser()
        )

    private fun getDetailedParticipant() = DetailedParticipantDto(
        activeOrders = emptyList(),
        completedOrders = emptyList(),
        investments = emptyList(),
        participant = getParticipantDto()
    )

    private fun getParticipantDto() = ParticipantDto(
        contestNumber = 1,
        totalValue = 0.00,
        totalInvestmentValue = 0.00,
        remainingFunds = 0.00
    )

    private fun getContest() = Contest(contestNumber = 1, startTime = LocalDateTime.now(), endTime = LocalDateTime.now())

    private fun getUser() = User(email = "test@mail.com", username = "username")
}