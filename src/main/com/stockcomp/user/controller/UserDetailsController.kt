package com.stockcomp.user.controller

import com.stockcomp.token.service.TokenService
import com.stockcomp.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserDetailsController(
    private val userService: UserService,
    private val tokenService: TokenService
) {

    @GetMapping("/get-details")
    fun getUserDetails(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<UserDetailsDto> =
        tokenService.extractEmailFromToken(jwt)
            .let { userService.findUserByTokenClaim(it) }
            .let { mapToUserDetailsDto(it) }
            .let { ResponseEntity.ok(it) }


    @PutMapping("/update-details")
    fun updateUserDetails(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody userDetailsDto: UserDetailsDto
    ): ResponseEntity<HttpStatus> =
        tokenService.extractEmailFromToken(jwt)
            .let { userService.findUserByTokenClaim(it) }
            .let { userService.updateUser(it, userDetailsDto) }
            .let { ResponseEntity(HttpStatus.OK) }
}