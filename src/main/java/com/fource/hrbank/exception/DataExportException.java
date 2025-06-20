package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class DataExportException extends BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.CSV_CONVERT_ERROR_MESSAGE;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.CSV_CONVERT_ERROR_MESSAGE;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}
