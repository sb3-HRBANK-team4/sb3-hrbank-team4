package com.fource.hrbank.repository.employee;

import com.fource.hrbank.domain.EmployeeStatus;
import java.time.LocalDate;

public interface EmployeeCustomRepository {
    long countByFilters(EmployeeStatus status, LocalDate from, LocalDate to);
}