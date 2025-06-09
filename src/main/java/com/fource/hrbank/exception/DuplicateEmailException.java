package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * 존재하는 이메일을 조회하거나 수정/삭제하려 할 때 발생하는 예외입니다.
 */
public class DuplicateEmailException extends BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.DUPLICATE_EMAIL;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.DUPLICATE_EMAIL;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}
