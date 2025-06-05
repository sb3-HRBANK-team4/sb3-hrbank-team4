package com.fource.hrbank.exception;

public class FileIOException extends RuntimeException {

    public static final String FILE_DEFAULT_ERROR_MESSAGE = "파일 처리 중 오류가 발생하였습니다.";
    public static final String FILE_SAVE_ERROR_MESSAGE = "파일 저장 중 오류가 발생했습니다.";
    public static final String FILE_READ_ERROR_MESSAGE = "파일을 읽는 중 오류가 발생하였습니다.";


    public FileIOException() {
        super(FILE_DEFAULT_ERROR_MESSAGE);
    }

    public FileIOException(String message) {
        super(message);
    }

    public FileIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
