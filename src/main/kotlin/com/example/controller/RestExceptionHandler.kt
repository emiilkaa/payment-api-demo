package com.example.controller

import com.example.model.HttpResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(value = [RuntimeException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun runtimeExceptionHandler(runtimeException: RuntimeException): ResponseEntity<HttpResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                HttpResponse(
                    success = false,
                    code = 500,
                    message = "Internal system error",
                    details = runtimeException.message.toString(),
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validateException(ex: MethodArgumentNotValidException): ResponseEntity<HttpResponse> {
        return invalidRequestResponse(ex)
    }

    @ExceptionHandler(NoSuchMethodError::class)
    fun validateException(ex: NoSuchMethodError): ResponseEntity<HttpResponse> {
        return invalidRequestResponse(ex)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun validateException(ex: IllegalArgumentException): ResponseEntity<HttpResponse> {
        return invalidRequestResponse(ex)
    }

    private fun invalidRequestResponse(ex: Throwable): ResponseEntity<HttpResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                HttpResponse(
                    success = false,
                    code = 400,
                    message = "Bad request",
                    details = ex.message.toString()
                )
            )

}
