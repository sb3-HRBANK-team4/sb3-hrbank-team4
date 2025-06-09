package com.fource.hrbank.exception;

import com.fource.hrbank.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * 존재하지 않는 직원을 조회하거나 수정/삭제하려 할 때 발생하는 예외입니다.
 */
public class EmployeeNotFoundException extends BaseException {

    private static final String MESSAGE = "존재하지 않는 직원입니다.";

    public EmployeeNotFoundException(Long employeeId) {
        super(MESSAGE, "직원 ID: " + employeeId, HttpStatus.NOT_FOUND);
    }

    public EmployeeNotFoundException(String details) {
        super(MESSAGE, details, HttpStatus.NOT_FOUND);
    }
}
