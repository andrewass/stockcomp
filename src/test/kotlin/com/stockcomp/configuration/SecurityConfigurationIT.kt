package com.stockcomp.configuration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerIntegrationTest
class SecurityConfigurationIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) {
        @Test
        fun `should allow unauthenticated health endpoint access`() {
            mockMvc
                .perform(get("/actuator/health"))
                .andExpect(status().isOk)
        }

        @Test
        fun `should allow unauthenticated prometheus endpoint access`() {
            mockMvc
                .perform(get("/actuator/prometheus"))
                .andExpect(status().isOk)
        }

        @Test
        fun `should require authentication for actuator env endpoint`() {
            mockMvc
                .perform(get("/actuator/env"))
                .andExpect(status().isUnauthorized)
        }

        @Test
        fun `should require authentication for business endpoints`() {
            mockMvc
                .perform(
                    get("/contests")
                        .queryParam("pageNumber", "0")
                        .queryParam("pageSize", "10"),
                ).andExpect(status().isUnauthorized)

            mockMvc
                .perform(get("/participants/contests"))
                .andExpect(status().isUnauthorized)

            mockMvc
                .perform(
                    post("/leaderboard/recalculations")
                        .queryParam("contestId", "1"),
                ).andExpect(status().isUnauthorized)
        }
    }
