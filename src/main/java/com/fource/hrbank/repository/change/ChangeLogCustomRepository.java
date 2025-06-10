package com.fource.hrbank.repository.change;

import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;

public interface ChangeLogCustomRepository {

    CursorPageResponseChangeLogDto searchChangeLogsWithSorting(
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
}