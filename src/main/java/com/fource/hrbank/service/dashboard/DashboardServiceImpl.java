package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeDistributionDto;
import com.fource.hrbank.dto.dashboard.EmployeeTrendDto;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
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

  @Override
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status) {
    long total = employeeRepository.countAllByStatus(status);  // 이미 있는 쿼리 메서드 사용

    return switch (groupBy) {
      case "department" -> toDistributionDto(employeeRepository.countByDepartmentGroup(status), total);
      case "position" -> toDistributionDto(employeeRepository.countByPositionGroup(status), total);
      default -> throw new IllegalArgumentException("지원하지 않는 groupBy 값: " + groupBy);
    };
  }

  private List<EmployeeDistributionDto> toDistributionDto(List<Object[]> raw, long totalCount) {
    return raw.stream()
        .map(row -> {
          String groupValue = (String) row[0];
          long count = (long) row[1];
          double ratio = totalCount > 0 ? (count * 100.0) / totalCount : 0.0;
          return new EmployeeDistributionDto(groupValue, Math.toIntExact(count), ratio);
        })
        .toList();
  }
}