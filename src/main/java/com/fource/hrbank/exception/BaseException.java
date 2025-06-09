package com.fource.hrbank.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public abstract class BaseException extends RuntimeException {

    public abstract HttpStatus getHttpStatus();
    public abstract String getMessage();
    public abstract String getDetails();
    public abstract Instant timestamp();
}
