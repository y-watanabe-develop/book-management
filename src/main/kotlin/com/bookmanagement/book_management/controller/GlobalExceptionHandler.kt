package com.bookmanagement.book_management.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(e: NoSuchElementException): Map<String, String?> =
        mapOf("error" to e.message)

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflict(e: IllegalStateException): Map<String, String?> =
        mapOf("error" to e.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationError(e: MethodArgumentNotValidException): Map<String, Any?> =
        mapOf(
            "error" to "Validation failed",
            "details" to e.bindingResult.fieldErrors.map {
                "${it.field}: ${it.defaultMessage}"
            }
        )
}
