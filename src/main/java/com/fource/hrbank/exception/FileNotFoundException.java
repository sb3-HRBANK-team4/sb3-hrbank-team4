package com.fource.hrbank.exception;

public class FileNotFoundException extends RuntimeException {

    public static final String FILE_NOT_FOUND_ERROR_MESSAGE = "요청하신 파일을 찾을 수 없습니다.";

    public FileNotFoundException() {
        super(FILE_NOT_FOUND_ERROR_MESSAGE);
    }

    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
