package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class DuplicateDepartmentException extends BaseException {

    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.DUPLICATE_DEPARTMENT;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }

    public DuplicateDepartmentException(String message) {
        this.message = message;
    }
}
