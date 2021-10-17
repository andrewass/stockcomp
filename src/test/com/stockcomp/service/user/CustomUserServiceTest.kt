package com.stockcomp.service.user

import com.stockcomp.domain.user.User
import com.stockcomp.exception.DuplicateCredentialException
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.SignUpRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CustomUserServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @SpyK
    private var passwordEncoder = BCryptPasswordEncoder()

    @InjectMockKs
    private lateinit var defaultUserService: DefaultUserService

    private val username = "testUser"
    private val password = "testPassword"
    private val email = "testEmail"
    private val country = "Canada"
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
    fun `should add new user from given request`() {
        val userSlot = slot<User>()
        every {
            userRepository.existsByUsername(username)
        } returns false

        every {
            userRepository.save(capture(userSlot))
        } returns user

        defaultUserService.signUpUser(SignUpRequest(username, password, email, country))

        verify { userRepository.save(any()) }
        Assertions.assertEquals(username, userSlot.captured.username)
        Assertions.assertEquals(userSlot.captured.password.length, 60)
        Assertions.assertEquals(email, userSlot.captured.email)
    }

    @Test
    fun `should throw exception when attempting to add user with duplicate username`() {
        every {
            userRepository.existsByUsername(username)
        } returns true

        assertThrows<DuplicateCredentialException> {
            defaultUserService.signUpUser(SignUpRequest(username, password, email, country))
        }
    }

    @Test
    fun `should get peristed user`() {
        val user = defaultUserService.findUserByUsername(username)!!

        Assertions.assertEquals(username, user.username)
        Assertions.assertEquals(password, user.password)
        Assertions.assertEquals(email, user.email)
    }
}