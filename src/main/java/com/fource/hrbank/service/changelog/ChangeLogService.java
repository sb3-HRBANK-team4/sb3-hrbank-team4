package com.fource.hrbank.service.changelog;

import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.ChangeLogCreateRequestDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import java.util.List;

public interface ChangeLogService {

    ChangeLogDto findById(Long id);

    CursorPageResponseChangeLogDto findAll(
        String employeeNumber,
        String type,
        String memo,
        String ipAddress,
        Long idAfter,
        String cursor,
        int size,
        String sortField,
        String sortDirection
    );

    List<ChangeDetailDto> findDiffs(Long id);

    ChangeLogDto create(Employee employee, ChangeType type, String memo, List<ChangeDetailDto> changeDetailDtos);

    List<ChangeDetailDto> detectChanges(Employee employee, EmployeeUpdateRequest request, Department department);
}