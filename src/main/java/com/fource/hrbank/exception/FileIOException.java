package com.fource.hrbank.exception;

import com.fource.hrbank.exception.common.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class FileIOException extends BaseException {

    public final String message;
    public final String details;

    public FileIOException(String message, String details) {
        this.message = message;
        this.details = details;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}
