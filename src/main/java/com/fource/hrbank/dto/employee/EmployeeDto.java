package com.fource.hrbank.dto.employee;

import java.util.Date;

public record EmployeeDto(
    long id,
    String name,
    String email,
    String employeeNumber,
    long departmentId,
    String departmentName,
    String position,
    Date hireDate,
    long profileImageId
) {

}
