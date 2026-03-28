package com.stockcomp.contest.internal

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = [ContestController::class])
class ContestExceptionHandler : ResponseEntityExceptionHandler() {
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
                    title = "Contest not found",
                    detail = exception.message ?: "Contest was not found",
                    type = "/problems/contest/not-found",
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
                    title = "Contest state conflict",
                    detail = exception.message ?: "Contest state transition is not allowed",
                    type = "/problems/contest/state-conflict",
                    instancePath = request.requestURI,
                ),
            )

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        val problemDetail =
            createProblemDetail(
                status = HttpStatus.BAD_REQUEST,
                title = "Invalid contest request",
                detail = "Request body validation failed",
                type = "/problems/contest/validation",
                instancePath = (request as? ServletWebRequest)?.request?.requestURI,
            ).also {
                it.setProperty(
                    "errors",
                    ex.bindingResult.fieldErrors.map { fieldError ->
                        mapOf(
                            "field" to fieldError.field,
                            "message" to (fieldError.defaultMessage ?: "Invalid value"),
                        )
                    },
                )
            }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

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
