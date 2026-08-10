package com.stockcomp.user
import com.stockcomp.user.internal.User
import com.stockcomp.user.internal.UserCreationService
import com.stockcomp.user.internal.UserIdentityService
import com.stockcomp.user.internal.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
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
    private val externalIdentity =
        ExternalIdentity(
            provider = IdentityProvider.GOOGLE,
            externalSubjectId = "testSubject",
            verifiedEmail = email,
        )
    private val user = User(username = username, email = email, country = "Canada")

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        userIdentityService = UserIdentityService(userRepository, userCreationService)
        every {
            userRepository.findByExternalIdentity(IdentityProvider.GOOGLE, "testSubject")
        } returns user
    }

    @Test
    fun `should get peristed user`() {
        val user = userIdentityService.findOrCreateUserByExternalIdentity(externalIdentity)

        Assertions.assertEquals(username, user.username)
        Assertions.assertEquals(email, user.email)
    }

    @Test
    fun `should link a stable subject to an existing account by verified email`() {
        every { userRepository.findByExternalIdentity(IdentityProvider.GOOGLE, "testSubject") } returns null
        every { userRepository.findByEmail(email) } returns user
        every { userRepository.save(user) } returns user

        val resolvedUser = userIdentityService.findOrCreateUserByExternalIdentity(externalIdentity)

        Assertions.assertEquals(user, resolvedUser)
        Assertions.assertEquals(1, user.userSubjects.size)
        Assertions.assertEquals("testSubject", user.userSubjects.single().externalSubjectId)
        verify(exactly = 1) { userRepository.save(user) }
    }

    @Test
    fun `should reject a new identity without a verified email`() {
        val identityWithoutVerifiedEmail = externalIdentity.copy(verifiedEmail = null)
        every { userRepository.findByExternalIdentity(IdentityProvider.GOOGLE, "testSubject") } returns null

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            userIdentityService.findOrCreateUserByExternalIdentity(identityWithoutVerifiedEmail)
        }
    }
}
