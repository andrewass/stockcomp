package com.stockcomp.profile.internal

import com.stockcomp.profile.UserProfileDto
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/users")
class UserProfileController(
    private val userProfileService: UserProfileService,
) {
    @GetMapping("/{userId}/profile")
    fun getUserProfile(
        @PathVariable @Positive userId: Long,
        @RequestParam(defaultValue = "0") @PositiveOrZero pageNumber: Int,
        @RequestParam(defaultValue = "20") @Positive @Max(100) pageSize: Int,
    ): ResponseEntity<UserProfileDto> = ResponseEntity.ok(userProfileService.getUserProfile(userId, pageNumber, pageSize))
}
