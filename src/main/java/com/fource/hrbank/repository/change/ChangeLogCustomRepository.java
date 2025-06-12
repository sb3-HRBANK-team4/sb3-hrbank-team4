package com.fource.hrbank.repository.change;

import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import java.time.Instant;

public interface ChangeLogCustomRepository {

    CursorPageResponse<ChangeLogDto> searchChangeLogsWithSorting(
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
}