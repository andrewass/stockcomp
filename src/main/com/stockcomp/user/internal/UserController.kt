package com.stockcomp.user.internal

import com.stockcomp.token.TokenService
import com.stockcomp.user.domain.mapToUserDetailsDto
import com.stockcomp.user.domain.mapToUserPageDto
import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserPageDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserServiceInternal,
    private val tokenService: TokenService
) {

    @GetMapping("/get-all-sorted")
    fun getAllUsersSortedByEmail(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<UserPageDto> =
        userService.getAllUsersSortedByEmail(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToUserPageDto(it)) }


    @GetMapping("/get-details")
    fun getUserDetails(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam username: String?
    ): ResponseEntity<UserDetailsDto> =
        tokenService.extractEmailFromToken(jwt)
            .let {
                username
                    ?.let { userService.findUserByUsername(username) }
                    ?: userService.findUserByTokenClaim(it)
            }
            .let { mapToUserDetailsDto(it) }
            .let { ResponseEntity.ok(it) }


    @PatchMapping("/update-details")
    fun updateUserDetails(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody userDetailsDto: UserDetailsDto
    ): ResponseEntity<HttpStatus> =
        tokenService.extractEmailFromToken(jwt)
            .let { userService.findUserByTokenClaim(it) }
            .let { userService.updateUser(it, userDetailsDto) }
            .let { ResponseEntity(HttpStatus.OK) }
}