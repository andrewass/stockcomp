package com.stockcomp.user.internal

import com.stockcomp.user.CreateUserRequest
import com.stockcomp.user.UserDto
import com.stockcomp.user.UserPageDto
import com.stockcomp.user.mapToUserDto
import com.stockcomp.user.mapToUserPageDto
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserServiceInternal,
) {
    @GetMapping("/sorted")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsersSortedByEmail(
        @RequestParam @PositiveOrZero pageNumber: Int,
        @RequestParam @Positive @Max(100) pageSize: Int,
    ): ResponseEntity<UserPageDto> = ResponseEntity.ok(mapToUserPageDto(userService.getAllUsersSortedByEmail(pageNumber, pageSize)))

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest,
    ): ResponseEntity<UserDto> = ResponseEntity.ok(mapToUserDto(userService.createUser(request.email)))
}
