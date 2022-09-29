package com.stockcomp.service.user

import com.stockcomp.user.entity.User
import com.stockcomp.user.repository.UserRepository
import com.stockcomp.user.service.DefaultUserService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CustomUserServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var defaultUserService: DefaultUserService

    private val username = "testUser"
    private val password = "testPassword"
    private val email = "testEmail"
    private val user = User(username = username, password = password, email = email, country = "Canada")

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)
        every {
            userRepository.findByUsername(username)
        } returns user
    }

    @Test
    fun `should load user by username`() {
        val userDetails = defaultUserService.loadUserByUsername(username)

        Assertions.assertEquals(username, userDetails.username)
        Assertions.assertEquals(password, userDetails.password)
    }

    @Test
    fun `should get peristed user`() {
        val user = defaultUserService.findUserByUsername(username)!!

        Assertions.assertEquals(username, user.username)
        Assertions.assertEquals(password, user.password)
        Assertions.assertEquals(email, user.email)
    }
}