package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class FileNotFoundException extends BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.FILE_NOT_FOUND_ERROR_MESSAGE;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.FILE_NOT_FOUND_ERROR_MESSAGE;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}
