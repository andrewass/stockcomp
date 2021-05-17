package com.stockcomp.controller.common

import com.stockcomp.service.CustomUserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Class for intercepting jwt from the requests to the server, and
 * setting a token as the authentication on the security context
 */
@Component
class TokenAuthenticationFilter(
    private val userService: CustomUserService
) : OncePerRequestFilter() {

    @Value("\${token.name}")
    lateinit var accessTokenName: String

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = getTokenFromCookie(request)
        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            val username = extractUsername(token)
            val userDetails = userService.loadUserByUsername(username)
            if (tokenIsValid(token, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun getTokenFromCookie(request: HttpServletRequest): String? {
        return request.cookies?.toList()
            ?.filter { it.name == accessTokenName }
            ?.map { it.value }
            ?.firstOrNull()
    }
}