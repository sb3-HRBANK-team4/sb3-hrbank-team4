package com.fource.hrbank.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChangeType {
    CREATED("직원 추가"),
    UPDATED("정보 수정"),
    DELETED("직원 삭제");

    private final String label;
}
