package com.stockcomp.user

import com.stockcomp.user.internal.User
import com.stockcomp.user.internal.UserCreationService
import com.stockcomp.user.internal.UserIdentityService
import com.stockcomp.user.internal.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CustomUserServiceTest {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var userCreationService: UserCreationService

    private lateinit var userIdentityService: UserIdentityService

    private val username = "testUser"
    private val email = "testEmail"
    private val userSubject = "testSubject"
    private val user = User(username = username, email = email, country = "Canada")

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        userIdentityService = UserIdentityService(userRepository, userCreationService)
        every {
            userRepository.findByUserSubject(userSubject)
        } returns user
    }

    @Test
    fun `should get peristed user`() {
        val user = userIdentityService.findOrCreateUserBySubject(userSubject)

        Assertions.assertEquals(username, user.username)
        Assertions.assertEquals(email, user.email)
    }
}
