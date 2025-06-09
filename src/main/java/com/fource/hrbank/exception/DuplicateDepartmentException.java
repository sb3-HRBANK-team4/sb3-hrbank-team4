package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class DuplicateDepartmentException extends BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.DUPLICATE_DEPARTMENT;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.DUPLICATE_DEPARTMENT;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}
