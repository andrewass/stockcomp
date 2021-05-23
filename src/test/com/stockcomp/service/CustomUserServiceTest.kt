package com.stockcomp.service

import com.stockcomp.domain.User
import com.stockcomp.exception.DuplicateCredentialException
import com.stockcomp.repository.jpa.UserRepository
import com.stockcomp.request.SignUpRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CustomUserServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @SpyK
    private var passwordEncoder = BCryptPasswordEncoder()

    @InjectMockKs
    private lateinit var userService: CustomUserService

    private val username = "testUser"
    private val password = "testPassword"
    private val email = "testEmail"
    private val user = User(username = username, password = password, email = email)

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)
        every {
            userRepository.findByUsername(username)
        } returns Optional.of(user)
    }

    @Test
    fun `should load user by username`() {
        val userDetails = userService.loadUserByUsername(username)

        assertEquals(username, userDetails.username)
        assertEquals(password, userDetails.password)
    }

    @Test
    fun `should add new user from given request`() {
        val userSlot = slot<User>()
        every {
            userRepository.existsByUsername(username)
        } returns false

        every {
            userRepository.save(capture(userSlot))
        } returns user

        userService.addNewUser(SignUpRequest(username, password, email))

        verify { userRepository.save(any<User>()) }
        assertEquals(username, userSlot.captured.username)
        assertEquals(userSlot.captured.password.length, 60)
        assertEquals(email, userSlot.captured.email)
    }

    @Test
    fun `should throw exception when attempting to add user with duplicate username`(){
        every {
            userRepository.existsByUsername(username)
        } returns true

        assertThrows<DuplicateCredentialException> {
            userService.addNewUser(SignUpRequest(username, password, email))
        }
    }

    @Test
    fun `should get peristed user`(){
        val user = userService.getPersistedUser(username)

        assertEquals(username, user.username)
        assertEquals(password, user.password)
        assertEquals(email, user.email)
    }
}