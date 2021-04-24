package com.stockcomp.controller.common

import org.springframework.http.ResponseCookie
import javax.servlet.http.HttpServletRequest


fun getJwtFromCookie(request: HttpServletRequest): String =
    request.cookies.filter { it.name == "jwt" }
        .map { it.value }.first()

fun createCookie(jwt: String, maxAge: Int): ResponseCookie =
    ResponseCookie.from("jwt", jwt)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(maxAge.toLong())
        .build()