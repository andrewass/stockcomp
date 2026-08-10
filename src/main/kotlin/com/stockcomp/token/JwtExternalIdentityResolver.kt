package com.stockcomp.token

import com.stockcomp.user.ExternalIdentity
import com.stockcomp.user.IdentityProvider
import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.stereotype.Component

@Component
class JwtExternalIdentityResolver {
    fun resolve(claimAccessor: ClaimAccessor): ExternalIdentity =
        ExternalIdentity(
            provider = IdentityProvider.GOOGLE,
            externalSubjectId = requiredClaim(claimAccessor, SUBJECT_CLAIM),
            verifiedEmail = verifiedEmail(claimAccessor),
        )

    private fun requiredClaim(
        claimAccessor: ClaimAccessor,
        claimName: String,
    ): String =
        claimAccessor.getClaimAsString(claimName)?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("JWT claim $claimName is required")

    private fun verifiedEmail(claimAccessor: ClaimAccessor): String? =
        claimAccessor
            .getClaimAsBoolean(EMAIL_VERIFIED_CLAIM)
            ?.takeIf { it }
            ?.let { claimAccessor.getClaimAsString(EMAIL_CLAIM)?.takeIf { email -> email.isNotBlank() } }

    companion object {
        private const val EMAIL_CLAIM = "email"
        private const val EMAIL_VERIFIED_CLAIM = "email_verified"
        private const val SUBJECT_CLAIM = "sub"
    }
}
