package com.stockcomp.authentication.service

import com.stockcomp.user.entity.User
import org.springframework.security.core.userdetails.UserDetails

interface JwtService {

    fun generateTokenPair(username: String): Pair<String, String>

    fun refreshTokenPair(refreshToken : String): Pair<String, String>

    fun accessTokenIsValid(token: String, userDetails: UserDetails): Boolean

    fun extractUsername(token: String): String

    fun deleteRefreshToken(user: User)
}