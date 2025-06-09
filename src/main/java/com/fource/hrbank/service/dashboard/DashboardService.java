package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeTrendDto;
import java.time.LocalDate;

public interface DashboardService {
    EmployeeTrendDto getEmployeeCount(EmployeeStatus status, LocalDate from, LocalDate to);
}