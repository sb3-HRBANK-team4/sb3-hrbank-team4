package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeCountResponseDto;
import com.fource.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final EmployeeRepository employeeRepository;

  @Override
  public EmployeeCountResponseDto getEmployeeCount(EmployeeStatus status, LocalDate from,
      LocalDate to) {
    long count = employeeRepository.countByFilters(status, from, to);
    return new EmployeeCountResponseDto(count);
  }
}