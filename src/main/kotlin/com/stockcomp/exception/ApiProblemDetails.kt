package com.stockcomp.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.validation.BindingResult
import java.net.URI

object ApiProblemDetails {
    fun create(
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

    fun fieldErrors(bindingResult: BindingResult): List<Map<String, String>> =
        bindingResult.fieldErrors.map { fieldError ->
            mapOf(
                "field" to fieldError.field,
                "message" to (fieldError.defaultMessage ?: "Invalid value"),
            )
        }

    fun constraintViolationErrors(exception: ConstraintViolationException): List<Map<String, String>> =
        exception.constraintViolations.map { violation ->
            mapOf(
                "path" to violation.propertyPath.toString(),
                "message" to violation.message,
            )
        }
}
