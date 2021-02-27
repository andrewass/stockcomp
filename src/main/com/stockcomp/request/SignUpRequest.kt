package com.stockcomp.request

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String
)