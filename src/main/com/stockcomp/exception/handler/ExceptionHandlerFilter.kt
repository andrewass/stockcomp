package com.stockcomp.exception.handler

import com.stockcomp.authentication.controller.createCookie
import com.stockcomp.authentication.controller.getAccessTokenFromCookie
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
        } catch (exception: ExpiredJwtException) {
            createCookie("accessToken", getAccessTokenFromCookie(request)!!, 0)
                .also {
                    response.addCookie(it)
                    response.status = HttpStatus.UNAUTHORIZED.value()
                }
        }
    }
}