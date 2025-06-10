package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeTrendDto;
import com.fource.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Logging
public class DashboardServiceImpl implements DashboardService {

  private final EmployeeRepository employeeRepository;

  @Override
  public EmployeeTrendDto getEmployeeCount(EmployeeStatus status, LocalDate from, LocalDate to) {
    // 현재 기간의 직원 수
    long count = employeeRepository.count();

    // 전월 대비 값 (임시로 0 지정)
    long change = 0;
    double changeRate = 0.0;

    // from이 null이 아니라면 yyyy-MM 형식으로 표시
    String dateString = (from != null) ? from.toString().substring(0, 7) : "unknown";

    return new EmployeeTrendDto(dateString, count, change, changeRate);
  }
}