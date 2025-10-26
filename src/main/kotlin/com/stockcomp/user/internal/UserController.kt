package com.stockcomp.user.internal

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserPageDto
import com.stockcomp.user.mapToUserPageDto
import com.stockcomp.user.toUserDetailsDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserServiceInternal,
) {
    @GetMapping("/sorted")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsersSortedByEmail(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): ResponseEntity<UserPageDto> =
        userService
            .getAllUsersSortedByEmail(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToUserPageDto(it)) }

    @GetMapping("/details")
    fun getUserDetails(
        @RequestParam userId: Long,
    ): ResponseEntity<UserDetailsDto> =
        toUserDetailsDto(userService.findUserById(userId))
            .let { ResponseEntity.ok(it) }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUserDetails(
        @TokenData tokenClaims: TokenClaims,
        @RequestBody userDetailsDto: UserDetailsDto,
    ): ResponseEntity<HttpStatus> =
        userService
            .updateUser(tokenClaims.userId, userDetailsDto)
            .let { ResponseEntity(HttpStatus.OK) }
}
