package com.stockcomp.controller

import com.stockcomp.dto.UserDetailsDto
import com.stockcomp.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/user")
class UserDetailsController(
    private val userService: UserService
) {

    @PutMapping("/update-details")
    fun updateUserDetails(
        httpServletRequest: HttpServletRequest,
        @RequestBody userDetailsDto: UserDetailsDto
    ) : ResponseEntity<HttpStatus> {
        userService.updateUserDetails(userDetailsDto)

        return ResponseEntity(HttpStatus.OK)
    }
}