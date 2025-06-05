package com.fource.hrbank.dto.employee;

import com.fource.hrbank.domain.EmployeeStatus;
import java.util.Date;

public record EmployeeUpdateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    Date hireDate,
    EmployeeStatus status,
    String memo
) {

}
