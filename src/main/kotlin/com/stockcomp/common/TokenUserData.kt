package com.stockcomp.common

data class TokenClaims(
    val userId: Long,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class TokenData
