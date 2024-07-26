package com.stockcomp.participant

import com.ninjasquad.springmockk.MockkBean
import com.stockcomp.configuration.SecurityConfiguration
import com.stockcomp.participant.participant.Participant
import com.stockcomp.participant.participant.ParticipantController
import com.stockcomp.participant.participant.ParticipantService
import com.stockcomp.participant.presentation.DetailedParticipantDto
import com.stockcomp.participant.presentation.ParticipantDto
import com.stockcomp.util.mockMvcGetRequest
import io.mockk.every
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Disabled
@Import(SecurityConfiguration::class)
@WebMvcTest(ParticipantController::class)
class ParticipantControllerTest(
    @Autowired val mockMvc: MockMvc
) {
    private val identifier = 123L
    private val basePath = "/participants"

    @MockkBean
    lateinit var participantService: ParticipantService

    @Test
    fun `should get participant for given contest`() {
        every { participantService.getParticipant(1, identifier) }
            .returns(Participant(userId = 1L, contestId = 1L))
        mockMvc.perform(
            mockMvcGetRequest("$basePath/contest")
                .queryParam("contestId", "1")
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
            .returns(PageImpl(listOf(Participant(userId = 1L, contestId = 1L))))
        mockMvc.perform(
            mockMvcGetRequest("$basePath/sorted")
                .queryParam("contestId", "1")
                .queryParam("pageNumber", "1")
                .queryParam("pageSize", "1")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should get participant history for user`() {
        every { participantService.getParticipantHistory("testUser") }
            .returns(listOf(Participant(contestId = 1L, userId = 1L)))
        mockMvc.perform(
            mockMvcGetRequest("$basePath/history")
                .queryParam("username", "testUser")
        ).andExpect(status().isOk)
    }

    private fun getDetailedParticipant() = DetailedParticipantDto(
        activeOrders = emptyList(),
        completedOrders = emptyList(),
        investments = emptyList(),
        participant = getParticipantDto()
    )

    private fun getParticipantDto() = ParticipantDto(
        totalValue = 0.00,
        totalInvestmentValue = 0.00,
        remainingFunds = 0.00
    )
}