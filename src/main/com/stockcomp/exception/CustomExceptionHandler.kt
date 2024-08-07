package com.stockcomp.exception

import com.stockcomp.exception.InsufficientFundsException
import com.stockcomp.exception.InvalidRoleException
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
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


    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(exception: IllegalStateException): ResponseEntity<Any> =
        ResponseEntity(exception.message, HttpStatus.CONFLICT)


    @ExceptionHandler(InvalidRoleException::class)
    fun handleExceptionsForForbidden(exception: InvalidRoleException) : ResponseEntity<Any> =
        ResponseEntity(exception.message, HttpStatus.FORBIDDEN)
}