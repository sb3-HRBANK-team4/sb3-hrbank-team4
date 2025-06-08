package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeCountResponseDto;
import com.fource.hrbank.repository.EmployeeRepository;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final EmployeeRepository employeeRepository;

  @Override
  public EmployeeCountResponseDto getEmployeeCount(EmployeeStatus status, Date from, Date to) {
    long count = employeeRepository.countByFilters(status, from, to);
    return new EmployeeCountResponseDto(count);
  }
}