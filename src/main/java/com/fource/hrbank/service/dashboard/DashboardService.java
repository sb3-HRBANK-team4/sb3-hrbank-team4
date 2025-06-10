package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeDistributionDto;
import com.fource.hrbank.dto.dashboard.EmployeeTrendDto;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    EmployeeTrendDto getEmployeeCount(EmployeeStatus status, LocalDate from, LocalDate to);

    List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);
}