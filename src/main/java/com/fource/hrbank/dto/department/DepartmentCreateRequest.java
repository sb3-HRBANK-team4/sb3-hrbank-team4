package com.fource.hrbank.dto.department;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Setter
@Getter
public class DepartmentCreateRequest {

    private String name;
    private String description;
    private LocalDate establishedDate;
}
