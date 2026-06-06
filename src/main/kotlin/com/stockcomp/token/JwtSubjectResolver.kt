package com.stockcomp.token

import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.stereotype.Component

@Component
class JwtSubjectResolver {
    fun resolveSubject(claimAccessor: ClaimAccessor): String =
        claimAccessor.getClaimAsString(EMAIL_CLAIM)?.takeIf { it.isNotBlank() }
            ?: claimAccessor.getClaimAsString(SUBJECT_CLAIM)?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Neither email nor sub claim was present in token")

    companion object {
        private const val EMAIL_CLAIM = "email"
        private const val SUBJECT_CLAIM = "sub"
    }
}
