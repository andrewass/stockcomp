package com.stockcomp.token

import com.stockcomp.user.AccountStatusAuthority
import com.stockcomp.user.ExternalIdentity
import com.stockcomp.user.IdentityProvider
import com.stockcomp.user.UserAuthenticationDetails
import com.stockcomp.user.UserRole
import com.stockcomp.user.UserServiceExternal
import com.stockcomp.user.UserStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.DisabledException
import org.springframework.security.oauth2.jwt.Jwt

class JwtRoleAuthoritiesConverterTest {
    private val userService = mockk<UserServiceExternal>()
    private val converter = JwtRoleAuthoritiesConverter(JwtExternalIdentityResolver(), userService)

    @Test
    fun `should resolve role using stable Google subject`() {
        val externalIdentity = googleIdentity(verifiedEmail = "user@example.com")
        every { userService.getUserAuthenticationDetails(externalIdentity) } returns
            UserAuthenticationDetails(
                role = UserRole.ADMIN,
                status = UserStatus.ACTIVE,
            )

        val authorities =
            converter.convert(
                jwt("email" to "user@example.com", "email_verified" to true, "sub" to "google-subject"),
            )

        assertEquals(listOf(AccountStatusAuthority.ACTIVE, "ROLE_ADMIN"), authorities.map { it.authority })
        verify(exactly = 1) { userService.getUserAuthenticationDetails(externalIdentity) }
    }

    @Test
    fun `should grant inactive self-service authority`() {
        every { userService.getUserAuthenticationDetails(googleIdentity()) } returns
            UserAuthenticationDetails(
                role = UserRole.USER,
                status = UserStatus.INACTIVE,
            )

        val authorities = converter.convert(jwt("sub" to "google-subject"))

        assertEquals(listOf(AccountStatusAuthority.INACTIVE), authorities.map { it.authority })
    }

    @Test
    fun `should reject suspended account`() {
        every { userService.getUserAuthenticationDetails(googleIdentity()) } returns
            UserAuthenticationDetails(
                role = UserRole.USER,
                status = UserStatus.SUSPENDED,
            )

        assertThrows(DisabledException::class.java) {
            converter.convert(jwt("sub" to "google-subject"))
        }
    }

    private fun googleIdentity(verifiedEmail: String? = null) =
        ExternalIdentity(
            provider = IdentityProvider.GOOGLE,
            externalSubjectId = "google-subject",
            verifiedEmail = verifiedEmail,
        )

    private fun jwt(vararg claims: Pair<String, Any>): Jwt {
        val builder =
            Jwt
                .withTokenValue("token")
                .header("alg", "none")
        claims.forEach { (name, value) -> builder.claim(name, value) }
        return builder.build()
    }
}
