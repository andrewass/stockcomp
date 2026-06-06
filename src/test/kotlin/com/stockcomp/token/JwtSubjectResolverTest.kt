package com.stockcomp.token

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt

class JwtSubjectResolverTest {
    private val resolver = JwtSubjectResolver()

    @Test
    fun `should prefer email claim over subject claim`() {
        val jwt = jwt("email" to "user@example.com", "sub" to "google-subject")

        assertEquals("user@example.com", resolver.resolveSubject(jwt))
    }

    @Test
    fun `should fall back to subject claim when email claim is missing`() {
        val jwt = jwt("sub" to "google-subject")

        assertEquals("google-subject", resolver.resolveSubject(jwt))
    }

    @Test
    fun `should reject tokens without supported subject claims`() {
        val jwt = jwt("name" to "Test User")

        assertThrows(IllegalStateException::class.java) {
            resolver.resolveSubject(jwt)
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
