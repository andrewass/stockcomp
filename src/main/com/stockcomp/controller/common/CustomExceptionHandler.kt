package com.stockcomp.controller.common

import com.stockcomp.exception.InsufficientFundsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@ControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(exception: NoSuchElementException): ResponseEntity<Any> =
        ResponseEntity(exception.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(InsufficientFundsException::class)
    fun handleInsufficientFundsException(exception: InsufficientFundsException): ResponseEntity<Any> =
        ResponseEntity(exception.message, HttpStatus.CONFLICT)
}