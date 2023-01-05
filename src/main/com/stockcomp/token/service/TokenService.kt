package com.stockcomp.token.service

import com.stockcomp.user.entity.User

interface TokenService {
    fun extractUserFromToken(token: String) : User
}