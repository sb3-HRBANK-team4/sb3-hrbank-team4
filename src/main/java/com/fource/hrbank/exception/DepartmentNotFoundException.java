package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class DepartmentNotFoundException extends BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.DEPARTMENT_NOT_FOUND;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.DEPARTMENT_NOT_FOUND;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}
