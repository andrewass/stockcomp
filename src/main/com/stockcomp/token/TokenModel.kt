package com.stockcomp.token

enum class TokenIssuer {
    CUSTOM_AUTH,
    GOOGLE
}

data class TokenClaims(
    val userIdentification: String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class TokenData