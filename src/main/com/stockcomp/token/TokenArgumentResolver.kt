package com.stockcomp.token

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.user.UserServiceExternal
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class TokenArgumentResolver(
    private val userService: UserServiceExternal
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.getParameterAnnotation(TokenData::class.java) != null

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val subject = (SecurityContextHolder.getContext().authentication.credentials as ClaimAccessor)
            .getClaimAsString("sub")

        return TokenClaims(userId = userService.getUserIdByEmail(subject))
    }
}