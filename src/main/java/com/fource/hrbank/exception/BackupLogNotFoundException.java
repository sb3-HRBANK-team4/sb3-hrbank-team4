package com.fource.hrbank.exception;

public class BackupLogNotFoundException extends RuntimeException {

    public static final String BACKUPLOG_NOT_FOUND_ERROR_MESSAGE = "요청하신 백업 이력을 찾을 수 없습니다.";

    public BackupLogNotFoundException() {
        super(BACKUPLOG_NOT_FOUND_ERROR_MESSAGE);
    }

    public BackupLogNotFoundException(String message) {
        super(message);
    }

    public BackupLogNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
