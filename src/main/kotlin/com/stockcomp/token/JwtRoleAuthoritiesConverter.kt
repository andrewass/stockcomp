package com.stockcomp.token

import com.stockcomp.user.UserServiceExternal
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class JwtRoleAuthoritiesConverter(
    private val jwtSubjectResolver: JwtSubjectResolver,
    private val userService: UserServiceExternal,
) : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(source: Jwt): Collection<GrantedAuthority> {
        val subject = jwtSubjectResolver.resolveSubject(source)
        val userRole = userService.getUserRole(subject)
        return listOf(SimpleGrantedAuthority("ROLE_$userRole"))
    }
}
