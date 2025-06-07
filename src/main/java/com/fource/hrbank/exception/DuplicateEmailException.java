package com.fource.hrbank.exception;

import com.fource.hrbank.exception.common.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BaseException {
    private static final String MESSAGE = "중복된 이메일입니다.";

    public DuplicateEmailException(String details) {
        super(MESSAGE, details, HttpStatus.BAD_REQUEST);
    }
}
