package com.stockcomp.user.internal

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.user.AccountSettingsDto
import com.stockcomp.user.UpdateAccountSettingsRequest
import com.stockcomp.user.toAccountSettingsDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/account")
class AccountController(
    private val userService: UserServiceInternal,
) {
    @GetMapping
    fun getAccountSettings(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<AccountSettingsDto> = ResponseEntity.ok(toAccountSettingsDto(userService.findUserById(tokenClaims.userId)))

    @PutMapping
    fun updateAccountSettings(
        @TokenData tokenClaims: TokenClaims,
        @Valid @RequestBody request: UpdateAccountSettingsRequest,
    ): ResponseEntity<AccountSettingsDto> = ResponseEntity.ok(toAccountSettingsDto(userService.updateUser(tokenClaims.userId, request)))

    @GetMapping("/admin")
    fun isAdmin(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<Boolean> = ResponseEntity.ok(userService.isAdmin(tokenClaims.userId))
}
