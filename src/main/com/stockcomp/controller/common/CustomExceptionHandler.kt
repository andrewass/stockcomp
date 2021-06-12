package com.stockcomp.controller.common

import com.stockcomp.exception.InsufficientFundsException
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(exception: NoSuchElementException): ResponseEntity<Any> =
        ResponseEntity(exception.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(InsufficientFundsException::class)
    fun handleInsufficientFundsException(exception: InsufficientFundsException): ResponseEntity<Any> =
        ResponseEntity(exception.message, HttpStatus.CONFLICT)

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleJwtExpiredException(exception: ExpiredJwtException): ResponseEntity<Any> =
        ResponseEntity("JWT Token expired", HttpStatus.UNAUTHORIZED)
}