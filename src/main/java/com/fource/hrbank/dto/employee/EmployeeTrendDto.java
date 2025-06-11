package com.fource.hrbank.dto.employee;

import java.time.LocalDate;

public record EmployeeTrendDto(
    LocalDate date,
    Long count,
    Long change,
    double changeRate
) {

}
