package com.fource.hrbank.dto.department;

import java.time.Instant;

public record DepartmentDto(
    Long id,
    String name,
    String description,
    Instant establishedDate,
    Long employeeCount
) {

}
