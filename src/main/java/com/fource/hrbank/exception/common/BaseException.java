package com.fource.hrbank.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final String details;
    private final HttpStatus httpStatus;

    public BaseException(String message, String details, HttpStatus httpStatus) {
        super(message);
        this.details = details;
        this.httpStatus = httpStatus;
    }
}
