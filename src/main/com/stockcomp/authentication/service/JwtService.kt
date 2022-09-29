package com.stockcomp.authentication.service

import org.springframework.security.core.userdetails.UserDetails

interface JwtService {
    fun accessTokenIsValid(token: String, userDetails: UserDetails): Boolean
}