package com.stockcomp.controller

import com.stockcomp.service.security.DefaultJwtService
import com.stockcomp.controller.common.createCookie
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest
import com.stockcomp.service.DefaultUserService
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class AuthenticationController internal constructor(
    private val authenticationManager: AuthenticationManager,
    private val userService: DefaultUserService,
    private val jwtService: DefaultJwtService,
    meterRegistry: MeterRegistry
) {
    @Value("\${jwt.cookie.duration}")
    private val cookieDuration: Int = 0

    private val signUpCounter: Counter = meterRegistry.counter("sign.up.counter")

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sign up a new user")
    fun signUpUser(@RequestBody request: SignUpRequest): ResponseEntity<HttpStatus> {
        userService.addNewUser(request)
        val tokenPair = jwtService.generateTokenPair(request.username)
        val cookie = createCookie(tokenPair.first, cookieDuration)
        signUpCounter.increment()

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build<HttpStatus>()
    }

    @PostMapping("/sign-in")
    @ApiOperation(value = "Sign in existing user")
    fun signInUser(@RequestBody request: AuthenticationRequest): ResponseEntity<HttpStatus> {
        authenticateUser(request.username, request.password)
        val tokenPair = jwtService.generateTokenPair(request.username)
        val cookie = createCookie(tokenPair.first, cookieDuration)

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build<HttpStatus>()
    }

    @PostMapping("/sign-out")
    @ApiOperation("Sign out signed in user")
    fun signOutUser(request: HttpServletRequest): ResponseEntity<HttpStatus> {
        val cookie = createCookie("", 0)

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build<HttpStatus>()
    }

    @GetMapping("/refresh-token")
    @ApiOperation("Refresh the access token")
    fun refreshToken(request: HttpServletRequest, response : HttpServletResponse,
                     @RequestParam username : String) : ResponseEntity<HttpStatus> {
        val tokenpair = jwtService.refreshTokenPair(username, "temptoken")

        return  ResponseEntity(HttpStatus.OK)
    }

    private fun authenticateUser(username: String, password: String): Authentication {
        val token = UsernamePasswordAuthenticationToken(username, password)

        return authenticationManager.authenticate(token)
    }
}