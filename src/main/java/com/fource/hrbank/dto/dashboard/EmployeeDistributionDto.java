package com.fource.hrbank.dto.dashboard;

public record EmployeeDistributionDto(
    String groupKey,   // 부서명
    int count,         // 해당 그룹 직원 수
    double percentage  // 전체 대비 비율 (%)
) {}