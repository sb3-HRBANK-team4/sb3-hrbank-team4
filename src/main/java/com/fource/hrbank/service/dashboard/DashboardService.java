package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeCountResponseDto;
import java.util.Date;

public interface DashboardService {
  EmployeeCountResponseDto getEmployeeCount(EmployeeStatus status, Date from, Date to);
}