package com.fource.hrbank.dto.backup;

import com.fource.hrbank.dto.department.DepartmentDto;
import java.util.List;

public record CursorPageResponseBackupDto(
        List<BackupDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements,
        boolean hasNext
) {}
