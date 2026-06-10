package com.stockcomp.user.internal

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
import java.net.URI

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = [UserController::class])
class UserExceptionHandler : ResponseEntityExceptionHandler() {
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
                    title = "User not found",
                    detail = exception.message ?: "User was not found",
                    type = "/problems/user/not-found",
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
                    title = "Invalid user request",
                    detail = "Request parameter validation failed",
                    type = "/problems/user/validation",
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

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        val problemDetail =
            createProblemDetail(
                status = HttpStatus.BAD_REQUEST,
                title = "Invalid user request",
                detail = "Request body validation failed",
                type = "/problems/user/validation",
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

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        val problemDetail =
            createProblemDetail(
                status = HttpStatus.BAD_REQUEST,
                title = "Invalid user request",
                detail = "Request body could not be parsed",
                type = "/problems/user/malformed-body",
                instancePath = (request as? ServletWebRequest)?.request?.requestURI,
            )
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
