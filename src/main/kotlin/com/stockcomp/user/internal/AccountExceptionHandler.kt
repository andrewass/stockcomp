package com.stockcomp.user.internal

import com.stockcomp.exception.ApiProblemDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = [AccountController::class])
class AccountExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        exception: NoSuchElementException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ApiProblemDetails.create(
                    status = HttpStatus.NOT_FOUND,
                    title = "Account not found",
                    detail = exception.message ?: "Account was not found",
                    type = "/problems/account/not-found",
                    instancePath = request.requestURI,
                ),
            )

    @ExceptionHandler(UsernameAlreadyExistsException::class)
    fun handleUsernameAlreadyExistsException(
        exception: UsernameAlreadyExistsException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiProblemDetails.create(
                    status = HttpStatus.CONFLICT,
                    title = "Username already exists",
                    detail = exception.message ?: "Username is already in use",
                    type = "/problems/account/username-conflict",
                    instancePath = request.requestURI,
                ),
            )

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        exception: ConstraintViolationException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiProblemDetails
                    .create(
                        status = HttpStatus.BAD_REQUEST,
                        title = "Invalid account request",
                        detail = "Request parameter validation failed",
                        type = "/problems/account/validation",
                        instancePath = request.requestURI,
                    ).also {
                        it.setProperty("errors", ApiProblemDetails.constraintViolationErrors(exception))
                    },
            )

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiProblemDetails
                .create(
                    status = HttpStatus.BAD_REQUEST,
                    title = "Invalid account request",
                    detail = "Request body validation failed",
                    type = "/problems/account/validation",
                    instancePath = (request as? ServletWebRequest)?.request?.requestURI,
                ).also {
                    it.setProperty("errors", ApiProblemDetails.fieldErrors(ex.bindingResult))
                },
        )

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiProblemDetails.create(
                status = HttpStatus.BAD_REQUEST,
                title = "Invalid account request",
                detail = "Request body could not be parsed",
                type = "/problems/account/malformed-body",
                instancePath = (request as? ServletWebRequest)?.request?.requestURI,
            ),
        )
}
