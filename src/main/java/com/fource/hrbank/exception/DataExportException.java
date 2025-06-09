package com.fource.hrbank.exception;

public class DataExportException extends RuntimeException {

    public static final String CSV_CONVERT_ERROR_MESSAGE = "CSV 파일 변환 중 에러가 발생하였습니다.";

    public DataExportException() {
        super(CSV_CONVERT_ERROR_MESSAGE);
    }

    public DataExportException(String message) {
        super(message);
    }

    public DataExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
