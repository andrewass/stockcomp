package com.stockcomp.configuration

import com.stockcomp.user.UserServiceExternal
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val userService: UserServiceExternal,
) {
    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    var jwkSetUri: String? = null

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
        httpSecurity
            .cors { it.configurationSource(createCorsConfiguration()) }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/task/*",
                        "/actuator/*",
                        "/user/*",
                        "/contest-operations/*",
                        "/swagger-ui/*",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                    ).permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
            }.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()

    private fun createCorsConfiguration(): CorsConfigurationSource {
        val courseConfiguration =
            CorsConfiguration().apply {
                addAllowedOrigin("http://localhost:8000")
                addAllowedOrigin("http://localhost:3000")
                addAllowedOrigin("http://stockclient-service:80")
                addAllowedOrigin("http://localhost:80")
                allowCredentials = true
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders =
                    listOf(
                        "Origin",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Expose-Headers",
                        "Content-Type",
                        "Accept",
                        "Authorization",
                        "Origin,Accept",
                        "X-Requested-With",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers",
                        "Range",
                    )
                exposedHeaders = listOf("Content-Range")
            }
        return UrlBasedCorsConfigurationSource().also {
            it.registerCorsConfiguration("/**", courseConfiguration)
        }
    }

    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val email = jwt.claims["email"] as String
            val userRole = userService.getUserRole(email)
            listOf(SimpleGrantedAuthority("ROLE_$userRole"))
        }
        return converter
    }
}
