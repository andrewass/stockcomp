package com.stockcomp.controller.common

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ExceptionHandlerFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (exception : ExpiredJwtException){
            val currentAccessToken = getAccessTokenFromCookie(request)
            val expiredCookie = createCookie("accessToken", currentAccessToken!!, 0)
            response.addCookie(expiredCookie)
            response.status = HttpStatus.UNAUTHORIZED.value()
        }
    }
}