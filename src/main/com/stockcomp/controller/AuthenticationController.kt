package com.stockcomp.controller

import com.stockcomp.controller.common.createCookie
import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.controller.common.getRefreshTokenFromCookie
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest
import com.stockcomp.service.security.DefaultJwtService
import com.stockcomp.service.user.DefaultUserService
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

    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sign up a new user")
    fun signUpUser(@RequestBody request: SignUpRequest, response: HttpServletResponse): ResponseEntity<HttpStatus> =
        userService.signUpUser(request)
            .let { jwtService.generateTokenPair(request.username) }
            .let {
                response.addCookie(createCookie(accessToken, it.first, cookieDuration))
                response.addCookie(createCookie(refreshToken, it.second, cookieDuration))
                signUpCounter.increment()
                ResponseEntity(HttpStatus.OK)
            }


    @PostMapping("/sign-in")
    @ApiOperation(value = "Sign in existing user")
    fun signInUser(
        @RequestBody request: AuthenticationRequest, response: HttpServletResponse
    ): ResponseEntity<String> =
        authenticateUser(request.username, request.password)
            .let { userService.signInUser(request) }
            .let {
                jwtService.generateTokenPair(request.username).let { pair ->
                    response.addCookie(createCookie(accessToken, pair.first, cookieDuration))
                    response.addCookie(createCookie(refreshToken, pair.second, cookieDuration))
                }
                ResponseEntity.ok(it)
            }


    @PostMapping("/sign-out")
    @ApiOperation("Sign out signed in user")
    fun signOutUser(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(request)
            .let { jwtService.generateTokenPair(it) }
            .let {
                response.addCookie(createCookie(accessToken, it.first, 0))
                response.addCookie(createCookie(refreshToken, it.second, 0))
                ResponseEntity(HttpStatus.OK)
            }


    @PostMapping("verify-admin")
    @ApiOperation("Verify requested user has admin role ")
    fun verifyAdmin(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Boolean> =
        extractUsernameFromRequest(request)
            .let { userService.verifyAdminUser(it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/refresh-token")
    @ApiOperation("Refresh the access token")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<HttpStatus> =
        getRefreshTokenFromCookie(request)
            .let { jwtService.refreshTokenPair(it) }
            .let {
                response.addCookie(createCookie(accessToken, it.first, cookieDuration))
                response.addCookie(createCookie(refreshToken, it.second, cookieDuration))
                ResponseEntity(HttpStatus.OK)
            }


    private fun authenticateUser(username: String, password: String) {
        UsernamePasswordAuthenticationToken(username, password)
            .also { authenticationManager.authenticate(it) }
    }

    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { jwtService.extractUsername(it!!) }
}