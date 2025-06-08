package com.fource.hrbank.dto.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fource.hrbank.domain.EmployeeStatus;
import java.util.Date;

public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    long departmentId,
    String departmentName,
    String position,

    // 프론트코드의 date 날짜 값 포맷 고정
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date hireDate,
    EmployeeStatus status,
    long profileImageId
) {

}
