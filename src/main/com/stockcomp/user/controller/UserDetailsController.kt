package com.stockcomp.user.controller

import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/user")
class UserDetailsController(
    private val userService: UserService
) {

    @GetMapping("/get-details")
    fun getUserDetails(@RequestParam email: String): ResponseEntity<UserDetailsDto> =
        userService.getUserDetails(email)
            .let { ResponseEntity.ok(it) }


    @PutMapping("/update-details")
    fun updateUserDetails(
        httpServletRequest: HttpServletRequest, @RequestBody userDetailsDto: UserDetailsDto
    ): ResponseEntity<HttpStatus> =
        userService.updateUserDetails(userDetailsDto)
            .run { ResponseEntity(HttpStatus.OK) }
}