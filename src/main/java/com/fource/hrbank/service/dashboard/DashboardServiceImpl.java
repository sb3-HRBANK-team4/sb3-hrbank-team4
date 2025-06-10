package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import com.fource.hrbank.dto.employee.EmployeeTrendDto;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Logging
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;

    // 직원 수 조회
    @Override
    public EmployeeTrendDto getEmployeeCount(EmployeeStatus status, LocalDate from, LocalDate to) {
      // 현재 기간의 직원 수
      long count = employeeRepository.count();

      // 전월 대비 값 (임시로 0 지정)
      long change = 0;
      double changeRate = 0.0;

      // from이 null이 아니라면 yyyy-MM 형식으로 표시
      LocalDate date = (from != null) ? from.withDayOfMonth(1) : LocalDate.of(1970, 1, 1);

      return new EmployeeTrendDto(date, count, change, changeRate);
    }
    @Override
    public long getEmployeeCountByDate(LocalDate date) {
      return employeeRepository.countByDateRange(date);
    }

    // 직원 수 분포 통계
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
            return new EmployeeDistributionDto(groupValue, count, ratio);
          })
          .toList();
    }
    // 직원 수 추이
      @Override
      public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {
        if (from == null || to == null) {
          to = LocalDate.now();
          from = to.minusMonths(11).withDayOfMonth(1); // 최근 12개월 기준
        }

        // 월 단위 분기: LocalDate 리스트 생성
        List<LocalDate> periods = getMonthlyPeriods(from, to);

        List<EmployeeTrendDto> results = new ArrayList<>();
        Long previousCount = null;

        for (LocalDate date : periods) {
          LocalDate start = date.withDayOfMonth(1);
          LocalDate end = start.plusMonths(1).minusDays(1);

          long count = employeeRepository.countByDateRange(end); // 시점 기준 직원 수

          long change = (previousCount != null) ? (count - previousCount) : 0;
          double percentage = (previousCount != null && previousCount > 0)
              ? (change * 100.0 / previousCount)
              : 0.0;

          results.add(new EmployeeTrendDto(start, count, change, percentage));
          previousCount = count;
        }

        return results;
    }
        private List<LocalDate> getMonthlyPeriods(LocalDate from, LocalDate to) {
          List<LocalDate> months = new ArrayList<>();
          LocalDate current = from.withDayOfMonth(1);
          while (!current.isAfter(to)) {
            months.add(current);
            current = current.plusMonths(1);
          }
          return months;
        }
}