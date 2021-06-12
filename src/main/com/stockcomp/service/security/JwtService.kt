package com.stockcomp.service.security

import org.springframework.security.core.userdetails.UserDetails

interface JwtService {

    fun generateTokenPair(username: String): String

    fun refreshTokenPair(username: String): String

    fun accessTokenIsValid(token: String, userDetails: UserDetails): Boolean

    fun extractUsername(token: String): String

    fun deleteRefreshToken(refreshToken: String): String
}