package com.fource.hrbank.repository.employee;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeCustomRepository {

    long countByStatus(EmployeeStatus status);

    List<EmployeeDistributionDto> getDistributionByGroup(String groupBy, EmployeeStatus status);

    Long countByFilters(EmployeeStatus status, LocalDate fromDate, LocalDate endDate);
}