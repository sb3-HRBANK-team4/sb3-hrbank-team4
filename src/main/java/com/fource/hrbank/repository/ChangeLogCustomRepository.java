package com.fource.hrbank.repository;

import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;

public interface ChangeLogCustomRepository {

    CursorPageResponseChangeLogDto searchChangeLogsWithSorting(
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
}