package com.fource.hrbank.dto.department;

import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

public record DepartmentDto (
        Long id,
        String name,
        String description,
        Instant establishedDate,
        Long employeeCount
){}
