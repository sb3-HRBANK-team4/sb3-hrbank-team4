package com.fource.hrbank.dto.common;

public interface ResponseDetails {

    public String DEPARTMENT_NOT_FOUND = "존재하지 않는 부서 입니다.";
    public String DUPLICATE_DEPARTMENT = "중복된 이름입니다..";
    public String EMPLOYEE_NOT_FOUND = "존재하지 않는 직원 입니다.";
    public String DUPLICATE_EMAIL = "중복된 이메일입니다.";
    public String BACKUPLOG_NOT_FOUND_ERROR_MESSAGE = "존재하지 않는 백업 이력입니다.";
    public String FILE_NOT_FOUND_ERROR_MESSAGE = "요청하신 파일을 찾을 수 없습니다.";
    public String CSV_CONVERT_ERROR_MESSAGE = "CSV 파일 변환 중 에러가 발생하였습니다.";
    public String FILE_DEFAULT_ERROR_MESSAGE = "파일 처리 중 오류가 발생하였습니다.";
    public String FILE_CREATE_ERROR_MESSAGE = "파일 생성 중 오류가 발생하였습니다.";
    public String FILE_SAVE_ERROR_MESSAGE = "파일 저장 중 오류가 발생했습니다.";
    public String FILE_READ_ERROR_MESSAGE = "파일을 읽는 중 오류가 발생하였습니다.";
    public String DEPARTMENT_DELETE_ERROR = "소속 직원이 있는 부서는 삭제할 수 없습니다.";
    public String CHANGELOG_NOT_FOUND = "존재하지 않는 수정 이력입니다";
    public String DUPLICATE_CHANGELOG = "중복된 수정 이력이 존재합니다.";
}
