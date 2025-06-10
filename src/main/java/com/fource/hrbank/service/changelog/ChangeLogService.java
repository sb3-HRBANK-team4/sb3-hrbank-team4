package com.fource.hrbank.service.changelog;

import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import java.util.List;

public interface ChangeLogService {

    CursorPageResponseChangeLogDto getAllChangeLogs(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Long idAfter,
        String cursor,
        int size,
        String sortField,
        String sortDirection
    );

    List<ChangeDetailDto> findDiffs(Long changeLogId);

    ChangeLogDto create(Employee employee, ChangeType type, String memo,
        List<ChangeDetailDto> changeDetailDtos);

    List<ChangeDetailDto> detectChanges(Employee employee, EmployeeUpdateRequest request,
        Department department);

    List<ChangeDetailDto> setChangeLogId(List<ChangeDetailDto> details, Long changeLogId);
}