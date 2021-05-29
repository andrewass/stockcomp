package com.stockcomp.request

data class AuthenticationRequest(
    val username: String,
    val password: String
)