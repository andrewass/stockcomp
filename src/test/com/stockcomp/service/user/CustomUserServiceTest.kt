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
    private val email = "testEmail"
    private val user = User(username = username, email = email, country = "Canada")

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        every {
            userRepository.findByEmail(email)
        } returns user
    }

    @Test
    fun `should get peristed user`() {
        val user = defaultUserService.findUserByEmail(email)!!

        Assertions.assertEquals(username, user.username)
        Assertions.assertEquals(email, user.email)
    }
}