package com.stockcomp.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    var jwkSetUri: String? = null

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .cors().configurationSource { createCorsConfiguration() }
            .and()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(
                "/task/*",
                "/actuator/*",
                "/user/*",
                "/contest-operations/*",
                "/swagger-ui/*",
                "/swagger-resources/**",
                "/v2/api-docs"
            ).permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer { obj: OAuth2ResourceServerConfigurer<HttpSecurity> -> obj.jwt() }
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        return httpSecurity.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()


    private fun createCorsConfiguration(): CorsConfiguration {
        return CorsConfiguration().apply {
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
    }
}