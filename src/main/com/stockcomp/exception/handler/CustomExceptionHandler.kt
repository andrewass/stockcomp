package com.stockcomp.exception.handler

import com.stockcomp.exception.InsufficientFundsException
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


    override fun handleHttpMessageNotReadable(
        exception: HttpMessageNotReadableException, headers: HttpHeaders,
        status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        logger.error(exception.message)
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }
}