package com.fource.hrbank.dto.employee;

import java.util.Date;

public record EmployeeCreateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    Date hireDate,
    String memo
) {

}
