package com.stockcomp.profile.internal

import com.stockcomp.exception.ApiProblemDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = [UserProfileController::class])
class UserProfileExceptionHandler : ResponseEntityExceptionHandler() {
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
                    title = "User profile not found",
                    detail = exception.message ?: "User profile was not found",
                    type = "/problems/profile/not-found",
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
                        title = "Invalid user profile request",
                        detail = "Request parameter validation failed",
                        type = "/problems/profile/validation",
                        instancePath = request.requestURI,
                    ).also {
                        it.setProperty("errors", ApiProblemDetails.constraintViolationErrors(exception))
                    },
            )
}
