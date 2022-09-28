package com.stockcomp.authentication.controller

import javax.servlet.http.HttpServletRequest


fun getAccessTokenFromCookie(request: HttpServletRequest): String? =
    request.cookies
        ?.filter { it.name == "accessToken" }
        ?.map { it.value }
        ?.first()
