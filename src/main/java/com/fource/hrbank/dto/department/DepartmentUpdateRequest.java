package com.fource.hrbank.dto.department;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentUpdateRequest {

    String name;
    String description;
    LocalDate establishedDate;
}
