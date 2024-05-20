package com.stockcomp.token

enum class TokenIssuer {
    CUSTOM_AUTH,
    GOOGLE
}

data class TokenData(
    val email: String
)