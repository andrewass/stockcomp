package com.stockcomp.controller.common

import javax.servlet.http.HttpServletRequest


fun getJwtFromCookie(request: HttpServletRequest): String? =
    request.cookies.filter { it.name == "jwt" }
        .map { it.value }.firstOrNull()