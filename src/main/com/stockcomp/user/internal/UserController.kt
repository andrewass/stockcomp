package com.stockcomp.user.internal

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserPageDto
import com.stockcomp.user.mapToUserDetailsDto
import com.stockcomp.user.mapToUserPageDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserServiceInternal
) {

    @GetMapping("/sorted")
    fun getAllUsersSortedByEmail(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<UserPageDto> =
        userService.getAllUsersSortedByEmail(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToUserPageDto(it)) }

    @GetMapping("/details")
    fun getUserDetails(
        @RequestParam userId: Long
    ): ResponseEntity<UserDetailsDto> =
        mapToUserDetailsDto(userService.findUserById(userId))
            .let { ResponseEntity.ok(it) }

    @PatchMapping("/update")
    fun updateUserDetails(
        @TokenData tokenClaims: TokenClaims,
        @RequestBody userDetailsDto: UserDetailsDto
    ): ResponseEntity<HttpStatus> =
            userService.updateUser(tokenClaims.userId, userDetailsDto)
            .let { ResponseEntity(HttpStatus.OK) }
}