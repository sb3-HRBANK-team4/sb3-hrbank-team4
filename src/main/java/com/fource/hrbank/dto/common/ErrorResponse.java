package com.fource.hrbank.dto.common;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String message,
    String details
) {

    public static ErrorResponse of(HttpStatus status, String details) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            details
        );
    }

    public static ErrorResponse of(HttpStatus status, String message, String details) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            message,
            details
        );
    }
}
