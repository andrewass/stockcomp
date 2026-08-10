package com.stockcomp.token

import com.stockcomp.user.ExternalIdentity
import com.stockcomp.user.IdentityProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt

class JwtExternalIdentityResolverTest {
    private val resolver = JwtExternalIdentityResolver()

    @Test
    fun `should resolve Google subject independently of email`() {
        val jwt = jwt("email" to "new-email@example.com", "email_verified" to true, "sub" to "google-subject")

        assertEquals(
            ExternalIdentity(
                provider = IdentityProvider.GOOGLE,
                externalSubjectId = "google-subject",
                verifiedEmail = "new-email@example.com",
            ),
            resolver.resolve(jwt),
        )
    }

    @Test
    fun `should expose email only when it is verified`() {
        val jwt = jwt("email" to "user@example.com", "email_verified" to false, "sub" to "google-subject")

        assertNull(resolver.resolve(jwt).verifiedEmail)
    }

    @Test
    fun `should reject tokens without a subject claim`() {
        val jwt = jwt("email" to "user@example.com", "email_verified" to true)

        assertThrows(IllegalStateException::class.java) {
            resolver.resolve(jwt)
        }
    }

    private fun jwt(vararg claims: Pair<String, Any>): Jwt {
        val builder = Jwt.withTokenValue("token").header("alg", "none")
        claims.forEach { (name, value) -> builder.claim(name, value) }
        return builder.build()
    }
}
