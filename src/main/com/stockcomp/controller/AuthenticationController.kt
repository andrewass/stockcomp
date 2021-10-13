package com.stockcomp.controller

import com.stockcomp.controller.common.createCookie
import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.controller.common.getRefreshTokenFromCookie
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest
import com.stockcomp.service.user.DefaultUserService
import com.stockcomp.service.security.DefaultJwtService
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
@Api(description = "Endpoints for user authentication")
class AuthenticationController internal constructor(
    private val authenticationManager: AuthenticationManager,
    private val userService: DefaultUserService,
    private val jwtService: DefaultJwtService,
    meterRegistry: MeterRegistry
) {
    @Value("\${cookie.duration}")
    private val cookieDuration: Int = 0

    private val signUpCounter: Counter = meterRegistry.counter("sign.up.counter")

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sign up a new user")
    fun signUpUser(@RequestBody request: SignUpRequest, response: HttpServletResponse): ResponseEntity<HttpStatus> {
        userService.signUpUser(request)
        val (accessToken, refreshToken) = jwtService.generateTokenPair(request.username)
        val accessTokenCookie = createCookie("accessToken", accessToken, cookieDuration)
        val refreshTokenCookie = createCookie("refreshToken", refreshToken, cookieDuration)
        signUpCounter.increment()
        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/sign-in")
    @ApiOperation(value = "Sign in existing user")
    fun signInUser(
        @RequestBody request: AuthenticationRequest, response: HttpServletResponse
    ): ResponseEntity<String> {
        authenticateUser(request.username, request.password)
        val userRole = userService.signInUser(request)
        val (accessToken, refreshToken) = jwtService.generateTokenPair(request.username)
        val accessTokenCookie = createCookie("accessToken", accessToken, cookieDuration)
        val refreshTokenCookie = createCookie("refreshToken", refreshToken, cookieDuration)
        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        return ResponseEntity.ok(userRole)
    }

    @PostMapping("/sign-out")
    @ApiOperation("Sign out signed in user")
    fun signOutUser(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<HttpStatus> {
        val currentAccessToken = getAccessTokenFromCookie(request)
        val username = currentAccessToken?.let { jwtService.extractUsername(currentAccessToken) }
        val (accessToken, refreshToken) = jwtService.generateTokenPair(username!!)
        val accessTokenCookie = createCookie("accessToken", accessToken, 0)
        val refreshTokenCookie = createCookie("refreshToken", refreshToken, 0)
        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/refresh-token")
    @ApiOperation("Refresh the access token")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<HttpStatus> {
        val currentRefreshToken = getRefreshTokenFromCookie(request)
        val (accessToken, refreshToken) = jwtService.refreshTokenPair(currentRefreshToken)
        val accessTokenCookie = createCookie("accessToken", accessToken, cookieDuration)
        val refreshTokenCookie = createCookie("refreshToken", refreshToken, cookieDuration)
        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        return ResponseEntity(HttpStatus.OK)
    }

    private fun authenticateUser(username: String, password: String) {
        val token = UsernamePasswordAuthenticationToken(username, password)
        authenticationManager.authenticate(token)
    }
}