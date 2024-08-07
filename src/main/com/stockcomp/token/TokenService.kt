package com.stockcomp.token

import com.stockcomp.common.TokenIssuer
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class TokenService {

    fun extractEmailFromToken(token: Jwt): String =
        when (extractIssuerFromToken()) {
            TokenIssuer.CUSTOM_AUTH -> extractEmailFromCustomAuth(token)
            else -> throw RuntimeException("Unsupported issuer")
        }

    private fun extractEmailFromCustomAuth(token: Jwt): String =
        token.getClaimAsString("sub")

    private fun extractIssuerFromToken() = TokenIssuer.CUSTOM_AUTH
}