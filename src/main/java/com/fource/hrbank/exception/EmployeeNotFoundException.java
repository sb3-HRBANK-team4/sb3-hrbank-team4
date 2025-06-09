package com.fource.hrbank.exception;

import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.common.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

/**
 * 존재하지 않는 직원을 조회하거나 수정/삭제하려 할 때 발생하는 예외입니다.
 */
public class EmployeeNotFoundException extends BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.EMPLOYEE_NOT_FOUND;
    }

    @Override
    public String getDetails() {
        return ResponseDetails.EMPLOYEE_NOT_FOUND;
    }

    @Override
    public Instant timestamp() {
        return Instant.now();
    }
}
