package com.stockcomp.participant

import com.ninjasquad.springmockk.MockkBean
import com.stockcomp.configuration.SecurityConfiguration
import com.stockcomp.token.TokenService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Import(SecurityConfiguration::class)
@WebMvcTest(ParticipantController::class)
class ParticipantControllerTest(
    @Autowired val mockMvc: MockMvc
) {
    @MockkBean
    lateinit var participantService: ParticipantService

    @MockkBean
    lateinit var tokenService: TokenService

    @Test
    fun test1() {
        mockMvc.perform(
            get("/participants/active")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
        ).andExpect(status().isOk())
    }
}