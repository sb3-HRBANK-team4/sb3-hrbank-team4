package com.fource.hrbank.service.employee;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeDto;

public interface EmployeeService {

    public EmployeeDto findById(Long id);

    public CursorPageResponseEmployeeDto findAll(
        String nameOrEmail, String departmentName, String position, EmployeeStatus status,
        String sortField, String sortDirection, String cursor, Long idAfter, int size);

}
