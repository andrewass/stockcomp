package com.stockcomp.leaderboard.internal

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
import java.net.URI

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = [LeaderboardController::class])
class LeaderboardExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        exception: NoSuchElementException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                createProblemDetail(
                    status = HttpStatus.NOT_FOUND,
                    title = "Leaderboard entry not found",
                    detail = exception.message ?: "Leaderboard entry was not found",
                    type = "/problems/leaderboard/not-found",
                    instancePath = request.requestURI,
                ),
            )

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(
        exception: IllegalStateException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                createProblemDetail(
                    status = HttpStatus.CONFLICT,
                    title = "Leaderboard state conflict",
                    detail = exception.message ?: "Leaderboard state is invalid",
                    type = "/problems/leaderboard/state-conflict",
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
                createProblemDetail(
                    status = HttpStatus.BAD_REQUEST,
                    title = "Invalid leaderboard request",
                    detail = "Request parameter validation failed",
                    type = "/problems/leaderboard/validation",
                    instancePath = request.requestURI,
                ).also {
                    it.setProperty(
                        "errors",
                        exception.constraintViolations.map { violation ->
                            mapOf(
                                "path" to violation.propertyPath.toString(),
                                "message" to violation.message,
                            )
                        },
                    )
                },
            )

    private fun createProblemDetail(
        status: HttpStatus,
        title: String,
        detail: String,
        type: String,
        instancePath: String?,
    ): ProblemDetail =
        ProblemDetail
            .forStatusAndDetail(status, detail)
            .also {
                it.title = title
                it.type = URI.create(type)
                if (instancePath != null) {
                    it.instance = URI.create(instancePath)
                }
            }
}
