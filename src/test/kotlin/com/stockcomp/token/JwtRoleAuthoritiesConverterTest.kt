package com.stockcomp.token

import com.stockcomp.user.AccountStatusAuthority
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
    private val converter = JwtRoleAuthoritiesConverter(JwtSubjectResolver(), userService)

    @Test
    fun `should resolve role using preferred email claim`() {
        every { userService.getUserAuthenticationDetails("user@example.com") } returns
            UserAuthenticationDetails(
                role = UserRole.ADMIN,
                status = UserStatus.ACTIVE,
            )

        val authorities =
            converter.convert(
                jwt("email" to "user@example.com", "sub" to "google-subject"),
            )

        assertEquals(listOf(AccountStatusAuthority.ACTIVE, "ROLE_ADMIN"), authorities.map { it.authority })
        verify(exactly = 1) { userService.getUserAuthenticationDetails("user@example.com") }
    }

    @Test
    fun `should resolve role using subject claim fallback`() {
        every { userService.getUserAuthenticationDetails("google-subject") } returns
            UserAuthenticationDetails(
                role = UserRole.USER,
                status = UserStatus.ACTIVE,
            )

        val authorities =
            converter.convert(
                jwt("sub" to "google-subject"),
            )

        assertEquals(listOf(AccountStatusAuthority.ACTIVE, "ROLE_USER"), authorities.map { it.authority })
        verify(exactly = 1) { userService.getUserAuthenticationDetails("google-subject") }
    }

    @Test
    fun `should grant inactive self-service authority`() {
        every { userService.getUserAuthenticationDetails("user@example.com") } returns
            UserAuthenticationDetails(
                role = UserRole.USER,
                status = UserStatus.INACTIVE,
            )

        val authorities = converter.convert(jwt("email" to "user@example.com"))

        assertEquals(listOf(AccountStatusAuthority.INACTIVE), authorities.map { it.authority })
    }

    @Test
    fun `should reject suspended account`() {
        every { userService.getUserAuthenticationDetails("user@example.com") } returns
            UserAuthenticationDetails(
                role = UserRole.USER,
                status = UserStatus.SUSPENDED,
            )

        assertThrows(DisabledException::class.java) {
            converter.convert(jwt("email" to "user@example.com"))
        }
    }

    private fun jwt(vararg claims: Pair<String, String>): Jwt {
        val builder =
            Jwt
                .withTokenValue("token")
                .header("alg", "none")
        claims.forEach { (name, value) -> builder.claim(name, value) }
        return builder.build()
    }
}
