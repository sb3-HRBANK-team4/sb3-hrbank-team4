package com.fource.hrbank.dto.department;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class DepartmentCreateRequest {

    private String name;
    private String description;
    private Instant establishedDate;
}
