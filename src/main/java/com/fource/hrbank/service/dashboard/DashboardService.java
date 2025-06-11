package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import com.fource.hrbank.dto.employee.EmployeeTrendDto;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    EmployeeTrendDto getEmployeeCount(EmployeeStatus status, LocalDate from, LocalDate to);

    List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);

    List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit);

    long getEmployeeCountByDate(LocalDate date);

}