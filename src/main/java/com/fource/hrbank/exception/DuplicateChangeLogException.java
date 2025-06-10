package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class DuplicateChangeLogException extends BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.DUPLICATE_CHANGELOG;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.DUPLICATE_CHANGELOG;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}