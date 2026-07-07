package com.stockcomp.token

import com.stockcomp.user.UserRole
import com.stockcomp.user.UserServiceExternal
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt

class JwtRoleAuthoritiesConverterTest {
    private val userService = mockk<UserServiceExternal>()
    private val converter = JwtRoleAuthoritiesConverter(JwtSubjectResolver(), userService)

    @Test
    fun `should resolve role using preferred email claim`() {
        every { userService.getUserRole("user@example.com") } returns UserRole.ADMIN

        val authorities =
            converter.convert(
                jwt("email" to "user@example.com", "sub" to "google-subject"),
            )

        assertEquals(listOf("ROLE_ADMIN"), authorities.map { it.authority })
        verify(exactly = 1) { userService.getUserRole("user@example.com") }
    }

    @Test
    fun `should resolve role using subject claim fallback`() {
        every { userService.getUserRole("google-subject") } returns UserRole.USER

        val authorities =
            converter.convert(
                jwt("sub" to "google-subject"),
            )

        assertEquals(listOf("ROLE_USER"), authorities.map { it.authority })
        verify(exactly = 1) { userService.getUserRole("google-subject") }
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
