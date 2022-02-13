package com.stockcomp.producer.common

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest


fun getAccessTokenFromCookie(request: HttpServletRequest): String? =
    request.cookies
        ?.filter { it.name == "accessToken" }
        ?.map { it.value }
        ?.first()

fun getRefreshTokenFromCookie(request: HttpServletRequest) : String =
    request.cookies
        .filter { it.name == "refreshToken" }
        .map { it.value }
        .first()

fun createCookie(name: String, value: String, maxAge: Int): Cookie =
    Cookie(name, value).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        setMaxAge(maxAge)
    }