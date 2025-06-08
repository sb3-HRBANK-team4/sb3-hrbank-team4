package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeCountResponseDto;
import java.time.LocalDate;

public interface DashboardService {
    EmployeeCountResponseDto getEmployeeCount(EmployeeStatus status, LocalDate from, LocalDate to);
}