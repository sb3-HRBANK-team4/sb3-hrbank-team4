package com.fource.hrbank.service.changelog;

import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;

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
}