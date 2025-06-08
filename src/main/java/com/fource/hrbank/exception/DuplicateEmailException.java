package com.fource.hrbank.exception;

import com.fource.hrbank.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * 존재하는 이메일을 조회하거나 수정/삭제하려 할 때 발생하는 예외입니다.
 */
public class DuplicateEmailException extends BaseException {
    private static final String MESSAGE = "중복된 이메일입니다.";

    public DuplicateEmailException(String details) {
        super(MESSAGE, details, HttpStatus.BAD_REQUEST);
    }
}
