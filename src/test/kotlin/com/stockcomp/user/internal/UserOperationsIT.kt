package com.stockcomp.user.internal

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPatchRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.user.CreateUserRequest
import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserPageDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerIntegrationTest
class UserOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val userService: UserServiceInternal,
    ) {
        private val mapper = jacksonObjectMapper()
        private val basePath = "/users"

        @Test
        fun `should create and fetch user details`() {
            val userId = createUser("user-ops@test.com")

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/details")
                            .queryParam("userId", userId.toString()),
                    ).andExpect(status().isOk)
                    .andReturn()

            val userDetails: UserDetailsDto = mapper.readValue(result.response.contentAsString)
            assertEquals(userId, userDetails.userId)
        }

        @Test
        fun `should return users sorted by email`() {
            createUser("alpha@test.com")

            val result =
                mockMvc
                    .perform(
                        mockMvcGetRequest("$basePath/sorted", "ADMIN")
                            .queryParam("pageNumber", "0")
                            .queryParam("pageSize", "10"),
                    ).andExpect(status().isOk)
                    .andReturn()

            val page: UserPageDto = mapper.readValue(result.response.contentAsString)
            assertTrue(page.totalEntriesCount >= 1)
        }

        @Test
        fun `should update current user details`() {
            val subjectUser = userService.findOrCreateUserBySubject("user")

            mockMvc
                .perform(
                    mockMvcPatchRequest("$basePath/update", "ADMIN")
                        .content(
                            mapper.writeValueAsString(
                                UserDetailsDto(
                                    userId = subjectUser.userId!!,
                                    username = "updated-user",
                                    fullName = "Updated User",
                                    country = "NO",
                                ),
                            ),
                        ),
                ).andExpect(status().isOk)

            val updated = userService.findUserById(subjectUser.userId!!)
            assertEquals("updated-user", updated.username)
        }

        @Test
        fun `should return admin flag`() {
            val subjectUser = userService.findOrCreateUserBySubject("user")

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/admin"))
                    .andExpect(status().isOk)
                    .andReturn()

            val isAdmin: Boolean = mapper.readValue(result.response.contentAsString)
            assertEquals(userService.isAdmin(subjectUser.userId!!), isAdmin)
        }

        private fun createUser(email: String): Long {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("$basePath/create", "ADMIN")
                            .content(mapper.writeValueAsString(CreateUserRequest(email))),
                    ).andExpect(status().isOk)
                    .andReturn()

            val response: com.stockcomp.user.UserDto = mapper.readValue(result.response.contentAsString)
            return response.userId
        }
    }
