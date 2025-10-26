package com.stockcomp.user

import com.stockcomp.user.internal.User
import com.stockcomp.user.internal.UserRepository
import com.stockcomp.user.internal.UserServiceInternal
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
    private lateinit var userServiceInternal: UserServiceInternal

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
        val user = userServiceInternal.findOrCreateUserByEmail(email)

        Assertions.assertEquals(username, user.username)
        Assertions.assertEquals(email, user.email)
    }
}
