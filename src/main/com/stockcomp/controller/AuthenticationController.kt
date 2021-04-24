package com.stockcomp.controller

import com.stockcomp.controller.common.createCookie
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest
import com.stockcomp.service.CustomUserService
import com.stockcomp.util.generateToken
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class AuthenticationController internal constructor(
    private val authenticationManager: AuthenticationManager, private val userService: CustomUserService,
    meterRegistry: MeterRegistry
) {
    @Value("\${token.expiration}")
    private val cookieDuration: String? = null

    private val signUpCounter: Counter

    init {
        signUpCounter = setupMetrics(meterRegistry)
    }

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sign up a new user")
    fun signUpUser(@RequestBody request: SignUpRequest): ResponseEntity<*> {
        userService.addNewUser(request)
        val jwt = generateToken(request.username)
        val cookie = createCookie(jwt, cookieDuration!!.toInt())
        signUpCounter.increment()

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build<Any>()
    }

    @PostMapping("/sign-in")
    @ApiOperation(value = "Sign in existing user")
    fun signInUser(@RequestBody request: AuthenticationRequest): ResponseEntity<*> {
        authenticateUser(request.username, request.password)
        val jwt = generateToken(request.username)
        val cookie = createCookie(jwt, cookieDuration!!.toInt())

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build<Any>()
    }

    @PostMapping("/sign-out")
    @ApiOperation(value = "Sign out signed in user")
    fun signOutUser(@RequestParam username: String): ResponseEntity<*> {
        val cookie = createCookie("", 0)

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build<Any>()
    }

    private fun authenticateUser(username: String, password: String): Authentication {
        val token = UsernamePasswordAuthenticationToken(username, password)

        return authenticationManager.authenticate(token)
    }

    private fun setupMetrics(meterRegistry: MeterRegistry): Counter {
        return meterRegistry.counter("sign.up.counter")
    }
}