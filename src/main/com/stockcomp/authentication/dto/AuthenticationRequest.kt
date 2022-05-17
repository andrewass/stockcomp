package com.stockcomp.authentication.dto

data class AuthenticationRequest(
    val username: String,
    val password: String
)