package com.stockcomp.user.internal

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPatchRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.configuration.mockMvcPutRequest
import com.stockcomp.user.AccountSettingsDto
import com.stockcomp.user.CreateUserRequest
import com.stockcomp.user.UpdateAccountSettingsRequest
import com.stockcomp.user.UserPageDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
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
        fun `should return signed in account settings including private email`() {
            val email = "user-ops@test.com"
            val userId = createUser(email)

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("/account", emailClaim = email))
                    .andExpect(status().isOk)
                    .andReturn()

            val account: AccountSettingsDto = mapper.readValue(result.response.contentAsString)
            assertEquals(userId, account.userId)
            assertEquals(email, account.email)
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
        fun `should return forbidden when non-admin lists users`() {
            mockMvc
                .perform(
                    mockMvcGetRequest("$basePath/sorted", "USER")
                        .queryParam("pageNumber", "0")
                        .queryParam("pageSize", "10"),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return forbidden when non-admin creates user`() {
            mockMvc
                .perform(
                    mockMvcPostRequest("$basePath/create", "USER")
                        .content(mapper.writeValueAsString(CreateUserRequest("non-admin-create@test.com"))),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return bad request for invalid create user payload`() {
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("$basePath/create", "ADMIN")
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
                        mockMvcGetRequest("$basePath/sorted", "ADMIN")
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

        @Test
        fun `should update current user details`() {
            val email = "account-update@test.com"
            val userId = createUser(email)

            val result =
                mockMvc
                    .perform(
                        mockMvcPutRequest("/account", emailClaim = email)
                            .content(
                                mapper.writeValueAsString(
                                    UpdateAccountSettingsRequest(
                                        username = "updated-user",
                                        fullName = "Updated User",
                                        country = "NO",
                                    ),
                                ),
                            ),
                    ).andExpect(status().isOk)
                    .andReturn()

            val response: AccountSettingsDto = mapper.readValue(result.response.contentAsString)
            val updated = userService.findUserById(userId)
            assertEquals("updated-user", updated.username)
            assertEquals(email, response.email)
        }

        @Test
        fun `should update only the account resolved from the token`() {
            val firstEmail = "first-account@test.com"
            val firstUserId = createUser(firstEmail)
            val secondUserId = createUser("second-account@test.com")
            val secondUsername = userService.findUserById(secondUserId).username

            mockMvc
                .perform(
                    mockMvcPutRequest("/account", emailClaim = firstEmail)
                        .content(
                            mapper.writeValueAsString(
                                UpdateAccountSettingsRequest(
                                    username = "first-updated",
                                    fullName = "First Updated",
                                    country = "NO",
                                ),
                            ),
                        ),
                ).andExpect(status().isOk)

            assertEquals("first-updated", userService.findUserById(firstUserId).username)
            assertEquals(secondUsername, userService.findUserById(secondUserId).username)
        }

        @Test
        fun `should return conflict when username is already in use`() {
            val firstEmail = "username-owner@test.com"
            createUser(firstEmail)
            val secondUserId = createUser("username-conflict@test.com")
            val conflictingUsername = userService.findUserById(secondUserId).username

            mockMvc
                .perform(
                    mockMvcPutRequest("/account", emailClaim = firstEmail)
                        .content(
                            mapper.writeValueAsString(
                                UpdateAccountSettingsRequest(
                                    username = conflictingUsername,
                                    fullName = null,
                                    country = null,
                                ),
                            ),
                        ),
                ).andExpect(status().isConflict)
                .andExpect(
                    org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                )
        }

        @Test
        fun `should reject invalid account settings`() {
            val email = "invalid-account@test.com"
            createUser(email)

            mockMvc
                .perform(
                    mockMvcPutRequest("/account", emailClaim = email)
                        .content(
                            mapper.writeValueAsString(
                                UpdateAccountSettingsRequest(
                                    username = " ",
                                    fullName = null,
                                    country = null,
                                ),
                            ),
                        ),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should return admin flag`() {
            val subjectUser = userService.findOrCreateUserBySubject("user")

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("/account/admin"))
                    .andExpect(status().isOk)
                    .andReturn()

            val isAdmin: Boolean = mapper.readValue(result.response.contentAsString)
            assertEquals(userService.isAdmin(subjectUser.userId!!), isAdmin)
        }

        @Test
        fun `should require authentication for account settings`() {
            mockMvc
                .perform(MockMvcRequestBuilders.get("/account"))
                .andExpect(status().isUnauthorized)
        }

        @Test
        fun `should remove legacy signed in user routes`() {
            mockMvc
                .perform(mockMvcGetRequest("$basePath/details").queryParam("userId", "1"))
                .andExpect(status().isNotFound)
            mockMvc
                .perform(mockMvcGetRequest("$basePath/admin"))
                .andExpect(status().isNotFound)
            mockMvc
                .perform(mockMvcPatchRequest("$basePath/update").content("{}"))
                .andExpect(status().isNotFound)
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
