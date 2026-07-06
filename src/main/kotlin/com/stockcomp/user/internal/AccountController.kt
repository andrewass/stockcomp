package com.stockcomp.user.internal

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.user.AccountSettingsDto
import com.stockcomp.user.UpdateAccountSettingsRequest
import com.stockcomp.user.UpdateAccountStatusRequest
import com.stockcomp.user.toAccountSettingsDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/account")
class AccountController(
    private val accountService: AccountService,
) {
    @GetMapping
    fun getAccountSettings(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<AccountSettingsDto> = ResponseEntity.ok(toAccountSettingsDto(accountService.getAccount(tokenClaims.userId)))

    @PutMapping
    fun updateAccountSettings(
        @TokenData tokenClaims: TokenClaims,
        @Valid @RequestBody request: UpdateAccountSettingsRequest,
    ): ResponseEntity<AccountSettingsDto> =
        ResponseEntity.ok(toAccountSettingsDto(accountService.updateAccount(tokenClaims.userId, request)))

    @GetMapping("/admin")
    fun isAdmin(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<Boolean> = ResponseEntity.ok(accountService.isAdmin(tokenClaims.userId))

    @PatchMapping("/status")
    fun updateAccountStatus(
        @TokenData tokenClaims: TokenClaims,
        @Valid @RequestBody request: UpdateAccountStatusRequest,
    ): ResponseEntity<AccountSettingsDto> =
        ResponseEntity.ok(
            toAccountSettingsDto(accountService.updateAccountStatus(tokenClaims.userId, request.newStatus)),
        )
}
