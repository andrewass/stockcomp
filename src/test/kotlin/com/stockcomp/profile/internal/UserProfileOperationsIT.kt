package com.stockcomp.profile.internal

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.configuration.mockMvcPostRequest
import com.stockcomp.configuration.mockMvcPutRequest
import com.stockcomp.profile.UserProfileDto
import com.stockcomp.user.UpdateAccountSettingsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@ControllerIntegrationTest
class UserProfileOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val jdbcTemplate: JdbcTemplate,
    ) {
        private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

        @Test
        fun `should return public profile with contest and leaderboard performance`() {
            val selectedUser = createUser()
            val tiedUser = createUser()
            val thirdUser = createUser()
            val fourthUser = createUser()
            updateAccount(selectedUser, "selected-user", "Selected User", "NO")
            val contestId = insertContestAwaitingCompletion("Profile Contest")
            insertParticipant(selectedUser.userId, contestId, BigDecimal("25000.00"))
            insertParticipant(tiedUser.userId, contestId, BigDecimal("25000.00"))
            insertParticipant(thirdUser.userId, contestId, BigDecimal("23000.00"))
            insertParticipant(fourthUser.userId, contestId, BigDecimal("21000.00"))

            mockMvc
                .perform(
                    mockMvcPostRequest("/leaderboard/recalculations", "ADMIN")
                        .queryParam("contestId", contestId.toString()),
                ).andExpect(status().isNoContent)

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("/users/${selectedUser.userId}/profile"))
                    .andExpect(status().isOk)
                    .andReturn()

            val response: UserProfileDto = mapper.readValue(result.response.contentAsString)
            val json = mapper.readTree(result.response.contentAsString)
            assertEquals(selectedUser.userId, response.userId)
            assertEquals("selected-user", response.username)
            assertEquals("Selected User", response.fullName)
            assertEquals("NO", response.country)
            assertFalse(json.has("email"))
            assertEquals(1, response.performance.completedContests)
            assertEquals(1, response.performance.wins)
            assertEquals(1, response.performance.podiums)
            assertBigDecimalEquals("1.00", response.performance.averageRank)
            assertBigDecimalEquals("25.00", response.performance.averageReturnPercentage)
            assertEquals(1, response.leaderboard.position)
            assertEquals(3, response.leaderboard.score)
            assertEquals(1, response.leaderboard.goldMedals)
            assertEquals(0, response.leaderboard.silverMedals)
            assertEquals(0, response.leaderboard.bronzeMedals)
            assertEquals(1L, response.contestHistory.totalEntriesCount)
            assertEquals(
                contestId,
                response.contestHistory.entries
                    .single()
                    .contestId,
            )
            assertBigDecimalEquals(
                "25000.00",
                response.contestHistory.entries
                    .single()
                    .finalPortfolioValue,
            )
            assertBigDecimalEquals(
                "5000.00",
                response.contestHistory.entries
                    .single()
                    .gainLoss,
            )
            assertBigDecimalEquals(
                "25.00",
                response.contestHistory.entries
                    .single()
                    .returnPercentage,
            )
        }

        @Test
        fun `should return empty performance for existing user without completed contests`() {
            val user = createUser()

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("/users/${user.userId}/profile"))
                    .andExpect(status().isOk)
                    .andReturn()

            val response: UserProfileDto = mapper.readValue(result.response.contentAsString)
            assertEquals(0, response.performance.completedContests)
            assertBigDecimalEquals("0.00", response.performance.averageRank)
            assertBigDecimalEquals("0.00", response.performance.averageReturnPercentage)
            assertEquals(null, response.leaderboard.position)
            assertEquals(0, response.leaderboard.score)
            assertTrue(response.contestHistory.entries.isEmpty())
            assertEquals(0L, response.contestHistory.totalEntriesCount)
        }

        @Test
        fun `should return profile problem details for unknown user and invalid paging`() {
            val notFound =
                mockMvc
                    .perform(mockMvcGetRequest("/users/999999/profile"))
                    .andExpect(status().isNotFound)
                    .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                    ).andReturn()
            assertEquals(
                "/problems/profile/not-found",
                mapper.readTree(notFound.response.contentAsString)["type"].asText(),
            )

            mockMvc
                .perform(
                    mockMvcGetRequest("/users/1/profile")
                        .queryParam("pageNumber", "-1")
                        .queryParam("pageSize", "101"),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should require authentication for public user profiles`() {
            mockMvc
                .perform(MockMvcRequestBuilders.get("/users/1/profile"))
                .andExpect(status().isUnauthorized)
        }

        private fun updateAccount(
            user: CreatedUser,
            username: String,
            fullName: String,
            country: String,
        ) {
            mockMvc
                .perform(
                    mockMvcPutRequest("/account", emailClaim = user.email)
                        .content(
                            mapper.writeValueAsString(
                                UpdateAccountSettingsRequest(
                                    username = username,
                                    fullName = fullName,
                                    country = country,
                                ),
                            ),
                        ),
                ).andExpect(status().isOk)
        }

        private fun createUser(): CreatedUser {
            val email = "profile-${UUID.randomUUID()}@test.com"
            val result =
                mockMvc
                    .perform(
                        mockMvcPostRequest("/users", "ADMIN")
                            .content("""{"email":"$email"}"""),
                    ).andExpect(status().isCreated)
                    .andReturn()
            return CreatedUser(
                userId = mapper.readTree(result.response.contentAsString)["userId"].asLong(),
                email = email,
            )
        }

        private fun insertContestAwaitingCompletion(contestName: String): Long =
            jdbcTemplate.queryForObject(
                """
                insert into t_contest (
                    contest_name,
                    start_time,
                    end_time,
                    contest_status,
                    date_created,
                    date_updated,
                    version
                )
                values (?, ?, ?, 'AWAITING_COMPLETION', current_timestamp, current_timestamp, 0)
                returning contest_id
                """.trimIndent(),
                Long::class.java,
                contestName,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().minusDays(1),
            )!!

        private fun insertParticipant(
            userId: Long,
            contestId: Long,
            totalValue: BigDecimal,
        ) {
            jdbcTemplate.update(
                """
                insert into t_participant (
                    contest_id,
                    user_id,
                    remaining_funds,
                    participant_rank,
                    total_value,
                    total_investment_value,
                    date_created,
                    date_updated,
                    version
                )
                values (?, ?, ?, null, ?, 0, current_timestamp, current_timestamp, 0)
                """.trimIndent(),
                contestId,
                userId,
                totalValue,
                totalValue,
            )
        }

        private fun assertBigDecimalEquals(
            expected: String,
            actual: BigDecimal,
        ) {
            assertEquals(0, BigDecimal(expected).compareTo(actual))
        }

        private data class CreatedUser(
            val userId: Long,
            val email: String,
        )
    }
