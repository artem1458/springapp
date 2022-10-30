package com.artem1458.recruitmentexercise.config

import com.artem1458.recruitmentexercise.exceptions.BadDataException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(value = [BadDataException::class])
  fun handleBadDataException(exception: RuntimeException, request: WebRequest): ResponseEntity<Any>? {
    if (exception is BadDataException)
      return handleExceptionInternal(
        exception,
        "BadDataException: ${exception.message}",
        HttpHeaders(),
        HttpStatus.BAD_REQUEST,
        request
      )

    return handleException(exception, request)
  }
}
