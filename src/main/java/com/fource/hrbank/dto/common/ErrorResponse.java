package com.fource.hrbank.dto.common;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

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
