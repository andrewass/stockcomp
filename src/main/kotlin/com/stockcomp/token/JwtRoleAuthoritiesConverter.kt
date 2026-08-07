package com.stockcomp.token

import com.stockcomp.user.AccountStatusAuthority
import com.stockcomp.user.UserServiceExternal
import com.stockcomp.user.UserStatus
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.DisabledException
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
        val authenticationDetails = userService.getUserAuthenticationDetails(subject)
        return when (authenticationDetails.status) {
            UserStatus.ACTIVE -> {
                listOf(
                    SimpleGrantedAuthority(AccountStatusAuthority.ACTIVE),
                    SimpleGrantedAuthority("ROLE_${authenticationDetails.role}"),
                )
            }

            UserStatus.INACTIVE -> {
                listOf(SimpleGrantedAuthority(AccountStatusAuthority.INACTIVE))
            }

            UserStatus.SUSPENDED -> {
                throw DisabledException("Account is suspended")
            }
        }
    }
}
