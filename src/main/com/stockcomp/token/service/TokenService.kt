package com.stockcomp.token.service

import org.springframework.security.oauth2.jwt.Jwt

interface TokenService {
    fun extractEmailFromToken(token: Jwt): String
}