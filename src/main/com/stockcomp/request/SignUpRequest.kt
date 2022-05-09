package com.stockcomp.request

import com.stockcomp.user.entity.Role

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
    var role: Role = Role.USER
)