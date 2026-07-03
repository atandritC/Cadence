package com.cadence.common.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> fieldErrors) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                fieldErrors);
        return ResponseEntity.status(status).body(body);
    }

    // 404 - resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    // 409 - duplicate
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException e) {
        return build(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    // 400 - validation failed
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    // 500 - anything unanticipated
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }

}
