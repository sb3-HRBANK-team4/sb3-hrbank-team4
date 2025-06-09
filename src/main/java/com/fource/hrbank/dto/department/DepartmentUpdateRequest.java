package com.fource.hrbank.dto.department;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DepartmentUpdateRequest {
    String name;
    String description;
    LocalDate establishedDate;
}
