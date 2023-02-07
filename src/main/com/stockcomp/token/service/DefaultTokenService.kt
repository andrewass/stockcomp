package com.stockcomp.token.service

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class DefaultTokenService : TokenService {

    override fun extractEmailFromToken(token: Jwt): String {
        return when (extractIssuerFromToken()) {
            TokenIssuer.CUSTOM_AUTH -> extractEmailFromCustomAuth(token)
            else -> throw RuntimeException("Unsupported issuer")
        }
    }

    private fun extractEmailFromCustomAuth(token: Jwt): String =
        token.getClaimAsString("sub")

    private fun extractIssuerFromToken() = TokenIssuer.CUSTOM_AUTH
}