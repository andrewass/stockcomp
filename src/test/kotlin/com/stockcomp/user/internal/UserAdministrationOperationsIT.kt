package com.stockcomp.user.internal

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.user.CreateUserRequest
import com.stockcomp.user.UserDto
import com.stockcomp.user.UserPageDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerIntegrationTest
class UserAdministrationOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) {
        private val mapper = jacksonObjectMapper()
        private val basePath = "/users"

        @Test
        fun `should return users sorted by email`() {
            createUser("alpha@test.com")

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest(basePath, "ADMIN")
                            .queryParam("pageNumber", "0")
                            .queryParam("pageSize", "10"),
                    ).andExpect(status().isOk)
                    .andReturn()

            val page: UserPageDto = mapper.readValue(result.response.contentAsString)
            assertTrue(page.totalEntriesCount >= 1)
        }

        @Test
        fun `should return forbidden when non-admin lists users`() {
            mockMvc
                .perform(
                    mockMvcGetRequest(basePath, "USER")
                        .queryParam("pageNumber", "0")
                        .queryParam("pageSize", "10"),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return forbidden when non-admin creates user`() {
            mockMvc
                .perform(
                    mockMvcPostRequest(basePath, "USER")
                        .content(mapper.writeValueAsString(CreateUserRequest("non-admin-create@test.com"))),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return bad request for invalid create user payload`() {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest(basePath, "ADMIN")
                            .content(mapper.writeValueAsString(CreateUserRequest("not-an-email"))),
                    ).andExpect(status().isBadRequest)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            assertEquals(400, response["status"].asInt())
            assertEquals("Invalid user request", response["title"].asText())
            assertEquals("/problems/user/validation", response["type"].asText())
            assertTrue(response["errors"].isArray)
        }

        @Test
        fun `should return bad request for invalid user pagination parameters`() {
            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest(basePath, "ADMIN")
                            .queryParam("pageNumber", "-1")
                            .queryParam("pageSize", "101"),
                    ).andExpect(status().isBadRequest)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            assertEquals(400, response["status"].asInt())
            assertEquals("Invalid user request", response["title"].asText())
            assertEquals("/problems/user/validation", response["type"].asText())
            assertTrue(response["errors"].isArray)
        }

        private fun createUser(email: String): Long {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest(basePath, "ADMIN")
                            .content(mapper.writeValueAsString(CreateUserRequest(email))),
                    ).andExpect(status().isCreated)
                    .andReturn()

            val response: UserDto = mapper.readValue(result.response.contentAsString)
            return response.userId
        }
    }
