package com.stockcomp.user.controller

import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.authentication.controller.getAccessTokenFromCookie
import com.stockcomp.authentication.service.JwtService
import com.stockcomp.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/user")
class UserDetailsController(
    private val userService: UserService,
    private val jwtService: JwtService
) {

    @GetMapping("/get-details")
    fun getUserDetails(
        httpServletRequest: HttpServletRequest, @RequestParam username: String?
    ): ResponseEntity<UserDetailsDto> =
        userService.getUserDetails(username ?: extractUsernameFromRequest(httpServletRequest))
            .let { ResponseEntity.ok(it) }


    @PutMapping("/update-details")
    fun updateUserDetails(
        httpServletRequest: HttpServletRequest, @RequestBody userDetailsDto: UserDetailsDto
    ): ResponseEntity<HttpStatus> =
        userService.updateUserDetails(userDetailsDto)
            .run { ResponseEntity(HttpStatus.OK) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { jwtService.extractUsername(it!!) }
}