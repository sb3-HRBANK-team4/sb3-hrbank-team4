package com.fource.hrbank.service.changelog;

import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.dto.changelog.DiffsDto;
import java.time.Instant;
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
        String sortDirection,
        Instant atFrom,
        Instant atTo
    );

    List<DiffsDto> findDiffs(Long changeLogId);

    ChangeLog create(Employee employee, ChangeType type, String memo,
        List<DiffsDto> diffsDtos);

    List<DiffsDto> createEmployeeDiffs(Employee before, Employee after);
}