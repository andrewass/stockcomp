package com.stockcomp.authentication.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class DefaultJwtService : JwtService {

    override fun accessTokenIsValid(token: String, userDetails: UserDetails): Boolean {
        return false
    }
}