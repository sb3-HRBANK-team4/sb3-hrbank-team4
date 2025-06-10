package com.fource.hrbank.dto.common;

public interface ResponseMessage {
    public String DEPARTMENT_NOT_FOUND = "부서 데이터 없음";
    public String DUPLICATE_DEPARTMENT = "부서 수정 실패.";
    public String EMPLOYEE_NOT_FOUND = "직원 데이터 없음";
    public String DUPLICATE_EMAIL = "중복된 이메일입니다.";
    public String BACKUPLOG_NOT_FOUND_ERROR_MESSAGE = "백업 이력 데이터 없음.";
    public String FILE_NOT_FOUND_ERROR_MESSAGE = "파일 데이터 없음.";
    public String CSV_CONVERT_ERROR_MESSAGE = "CSV 파일 변환 에러.";
    public String FILE_DEFAULT_ERROR_MESSAGE = "파일 처리 중 오류가 발생하였습니다.";
    public String FILE_CREATE_ERROR_MESSAGE = "파일 생성 중 오류가 발생하였습니다.";
    public String FILE_SAVE_ERROR_MESSAGE = "파일 저장 중 오류가 발생했습니다.";
    public String FILE_READ_ERROR_MESSAGE = "파일을 읽는 중 오류가 발생하였습니다.";
    public String DEPARTMENT_DELETE_ERROR = "부서 삭제 실패.";
    public String CHANGELOG_NOT_FOUND = "존재하지 않는 수정 이력입니다";
    public String DUPLICATE_CHANGELOG = "중복된 수정 이력이 존재합니다.";
}