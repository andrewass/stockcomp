package com.stockcomp.authentication.consumer

import com.stockcomp.authentication.dto.TokenValidationResponse

interface AuthorizationConsumer {

    fun validateAccessToken(token: String) : TokenValidationResponse
}