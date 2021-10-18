package com.stockcomp.request

import com.stockcomp.domain.user.Role

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
    var role: Role = Role.USER
)