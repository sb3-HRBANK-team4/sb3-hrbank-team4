package com.fource.hrbank.dto.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fource.hrbank.domain.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeDto(
    long id,
    String name,
    String email,
    String employeeNumber,
    long departmentId,
    String departmentName,
    String position,

    // date 날짜 값 포맷 고정
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate hireDate,
    EmployeeStatus status,
    long profileImageId
) {

}
