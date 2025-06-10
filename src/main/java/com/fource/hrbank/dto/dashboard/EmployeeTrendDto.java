package com.fource.hrbank.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeTrendDto {
    private String date;       // yyyy-MM 형태의 문자열 (예: "2025-06")
    private long count;        // 해당 월의 총 직원 수
    private long change;       // 전월 대비 변화량
    private double changeRate; // 전월 대비 변화율 (예: -12.5)
}