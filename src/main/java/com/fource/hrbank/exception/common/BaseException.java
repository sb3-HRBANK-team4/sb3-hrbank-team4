package com.fource.hrbank.exception.common;

import java.time.Instant;
import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    public abstract HttpStatus getHttpStatus();

    public abstract String getMessage();

    public abstract String getDetails();

    public abstract Instant timestamp();
}
