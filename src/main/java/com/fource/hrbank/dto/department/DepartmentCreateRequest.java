package com.fource.hrbank.dto.department;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DepartmentCreateRequest {

    private String name;
    private String description;
    private Instant establishedDate;
}
