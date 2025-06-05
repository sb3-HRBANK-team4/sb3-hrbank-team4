package com.fource.hrbank.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChangeType {
    ADD_EMPLOYEE("직원 추가"),
    UPDATED_INFO("정보 수정"),
    DELETE_EMPLOYEE("직원 삭제");

    private final String label;
}
