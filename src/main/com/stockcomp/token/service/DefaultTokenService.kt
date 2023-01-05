package com.stockcomp.token.service

import com.stockcomp.user.entity.User
import com.stockcomp.user.service.UserService
import org.springframework.stereotype.Service

@Service
class DefaultTokenService(
    private val userService : UserService
): TokenService {

    override fun extractUserFromToken(token: String) : User {
        return userService.findUserByEmail("email")
    }

    private fun extractIssuerFromToken(token: String) : TokenIssuer {
        return TokenIssuer.CUSTOM_AUTH
    }
}