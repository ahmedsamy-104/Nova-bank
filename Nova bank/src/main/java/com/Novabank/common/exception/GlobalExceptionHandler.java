package com.Novabank.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {

        List<FieldError> springFieldErrors = ex.getBindingResult().getFieldErrors();

        boolean isPasswordMismatch = springFieldErrors.stream()
                .anyMatch(fe -> "confirmPassword".equals(fe.getField())
                        || (fe.getDefaultMessage() != null && fe.getDefaultMessage().contains("Passwords do not match")));

        String errorCode = isPasswordMismatch ? "PASSWORD_MISMATCH" : "VALIDATION_FAILED";

        List<ErrorResponse.FieldError> fieldErrors = springFieldErrors.stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                request.getRequestURI(),
                errorCode,
                "Validation failed",
                LocalDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex,
                                                                 HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                request.getRequestURI(),
                ex.getErrorCode(),
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                      HttpServletRequest request) {
        String rootMessage = ex.getMostSpecificCause().getMessage().toLowerCase();

        String errorCode;
        String errorMessage;
        if (rootMessage.contains("email")) {
            errorCode = "EMAIL_ALREADY_REGISTERED";
            errorMessage = "Email already registered";
        } else if (rootMessage.contains("mobile")) {
            errorCode = "MOBILE_ALREADY_REGISTERED";
            errorMessage = "Mobile number already registered";
        } else {
            errorCode = "DUPLICATE_RESOURCE";
            errorMessage = "A record with the provided details already exists";
        }

        ErrorResponse body = new ErrorResponse(
                request.getRequestURI(),
                errorCode,
                errorMessage,
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex,
                                                             HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                request.getRequestURI(),
                "MALFORMED_REQUEST",
                "Malformed JSON request body",
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);

        ErrorResponse body = new ErrorResponse(
                request.getRequestURI(),
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    public static class DuplicateResourceException extends RuntimeException {

        private final String errorCode;

        public DuplicateResourceException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }

    // ===== Nested: standard error response body =====
    public static class ErrorResponse {

        private String apiPath;
        private String errorCode;
        private String errorMessage;
        private LocalDateTime errorTime;
        private List<FieldError> fieldErrors;

        public ErrorResponse(String apiPath, String errorCode, String errorMessage,
                             LocalDateTime errorTime, List<FieldError> fieldErrors) {
            this.apiPath = apiPath;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.errorTime = errorTime;
            this.fieldErrors = fieldErrors;
        }

        public String getApiPath() { return apiPath; }
        public String getErrorCode() { return errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public LocalDateTime getErrorTime() { return errorTime; }
        public List<FieldError> getFieldErrors() { return fieldErrors; }

        public static class FieldError {
            private String field;
            private String message;

            public FieldError(String field, String message) {
                this.field = field;
                this.message = message;
            }

            public String getField() { return field; }
            public String getMessage() { return message; }
        }
    }
}