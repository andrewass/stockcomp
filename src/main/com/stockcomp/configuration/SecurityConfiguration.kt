package com.stockcomp.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    var jwkSetUri: String? = null

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
        httpSecurity
            .cors { it.configurationSource(createCorsConfiguration()) }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/task/*",
                    "/actuator/*",
                    "/user/*",
                    "/contest-operations/*",
                    "/swagger-ui/*",
                    "/swagger-resources/**",
                    "/v2/api-docs"
                ).permitAll().anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt { } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()

    private fun createCorsConfiguration(): CorsConfigurationSource {
        val courseConfiguration = CorsConfiguration().apply {
            addAllowedOrigin("http://localhost:8000")
            addAllowedOrigin("http://localhost:3000")
            addAllowedOrigin("http://stockclient-service:80")
            addAllowedOrigin("http://localhost:80")
            addAllowedOrigin("http://stockcompclient.io")
            allowCredentials = true
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf(
                "Origin", "Access-Control-Allow-Origin", "Access-Control-Expose-Headers",
                "Content-Type", "Accept", "Authorization", "Origin,Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers", "Range"
            )
            exposedHeaders = listOf("Content-Range")
        }
        return UrlBasedCorsConfigurationSource().also {
            it.registerCorsConfiguration("/**", courseConfiguration)
        }
    }
}