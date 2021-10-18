package com.stockcomp.controller

import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.dto.UserDetailsDto
import com.stockcomp.service.security.JwtService
import com.stockcomp.service.user.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/user")
@Api(description = "Endpoints for updating and fetching user details")
class UserDetailsController(
    private val userService: UserService,
    private val jwtService: JwtService
) {

    @GetMapping("/get-details")
    @ApiOperation(value = "Get user details for a given username")
    fun getUserDetails(
        httpServletRequest: HttpServletRequest,
        @RequestParam username: String?
    ): ResponseEntity<UserDetailsDto> {
        val userDetails = userService.getUserDetails(
            username ?: extractUsernameFromRequest(httpServletRequest)
        )
        return ResponseEntity.ok(userDetails)
    }

    @PutMapping("/update-details")
    @ApiOperation(value = "Update user details for a given user")
    fun updateUserDetails(
        httpServletRequest: HttpServletRequest,
        @RequestBody userDetailsDto: UserDetailsDto
    ): ResponseEntity<HttpStatus> {
        userService.updateUserDetails(userDetailsDto)

        return ResponseEntity(HttpStatus.OK)
    }

    private fun extractUsernameFromRequest(request: HttpServletRequest): String {
        val jwt = getAccessTokenFromCookie(request)

        return jwtService.extractUsername(jwt!!)
    }
}