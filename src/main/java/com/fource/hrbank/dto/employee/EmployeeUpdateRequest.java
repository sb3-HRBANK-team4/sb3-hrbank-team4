package com.fource.hrbank.dto.employee;

import com.fource.hrbank.domain.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    EmployeeStatus status,
    String memo
) {

}
