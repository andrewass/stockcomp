package com.stockcomp.common

enum class TokenIssuer {
    CUSTOM_AUTH,
}

data class TokenClaims(
    val userId: Long,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class TokenData
