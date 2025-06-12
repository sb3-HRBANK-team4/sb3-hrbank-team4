package com.fource.hrbank.service.dashboard;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import com.fource.hrbank.dto.employee.EmployeeTrendDto;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Logging
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeTrendDto getEmployeeCount(EmployeeStatus status, LocalDate from, LocalDate to) {
      long count = employeeRepository.count();
      long change = 0;
      double changeRate = 0.0;

      LocalDate date = (from != null) ? from : LocalDate.of(1970, 1, 1);
      return new EmployeeTrendDto(date, count, change, changeRate);
    }

    @Override
    public long getEmployeeCountByDate(LocalDate date) {
      return employeeRepository.countByDateRange(date);
    }

    @Override
    public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status) {
      long total = employeeRepository.countAllByStatus(status);

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

    @Override
    public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {
      if (from == null || to == null) {
        to = LocalDate.now();
        from = to.minusMonths(11).withDayOfMonth(1);
      }

      List<LocalDate> periods = switch (unit) {
        case "day" -> getDailyPeriods(from, to);
        case "week" -> getWeeklyPeriods(from, to);
        case "quarter" -> getQuarterlyPeriods(from, to);
        case "year" -> getYearlyPeriods(from, to);
        default -> getMonthlyPeriods(from, to);
      };

      List<EmployeeTrendDto> results = new ArrayList<>();
      Long previousCount = null;

      for (LocalDate start : periods) {
        LocalDate end = switch (unit) {
          case "day" -> start;
          case "week" -> start.plusDays(6);
          case "quarter" -> start.plusMonths(3).minusDays(1);
          case "year" -> start.plusYears(1).minusDays(1);
          default -> start.plusMonths(1).minusDays(1);
        };

        long count = employeeRepository.countByDateRange(end);
        long change = (previousCount != null) ? (count - previousCount) : 0;
        double percentage = (previousCount != null && previousCount > 0)
            ? (change * 100.0 / previousCount)
            : 0.0;

        results.add(new EmployeeTrendDto(start, count, change, percentage));
        previousCount = count;
      }

      return results;
    }

    private List<LocalDate> getDailyPeriods(LocalDate from, LocalDate to) {
      List<LocalDate> days = new ArrayList<>();
      LocalDate current = from;
      while (!current.isAfter(to)) {
        days.add(current);
        current = current.plusDays(1);
      }
      return days;
    }

    private List<LocalDate> getWeeklyPeriods(LocalDate from, LocalDate to) {
      List<LocalDate> weeks = new ArrayList<>();
      LocalDate current = from.with(DayOfWeek.MONDAY);
      while (!current.isAfter(to)) {
        weeks.add(current);
        current = current.plusWeeks(1);
      }
      return weeks;
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

    private List<LocalDate> getQuarterlyPeriods(LocalDate from, LocalDate to) {
      List<LocalDate> quarters = new ArrayList<>();
      int month = ((from.getMonthValue() - 1) / 3) * 3 + 1;
      LocalDate current = LocalDate.of(from.getYear(), month, 1);
      while (!current.isAfter(to)) {
        quarters.add(current);
        current = current.plusMonths(3);
      }
      return quarters;
    }

    private List<LocalDate> getYearlyPeriods(LocalDate from, LocalDate to) {
      List<LocalDate> years = new ArrayList<>();
      LocalDate current = from.withDayOfYear(1);
      while (!current.isAfter(to)) {
        years.add(current);
        current = current.plusYears(1);
      }
      return years;
    }
}