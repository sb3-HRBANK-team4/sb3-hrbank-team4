package com.fource.hrbank.dto.backup;

import com.fource.hrbank.domain.BackupStatus;

import java.time.Instant;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    Long fileId
) {
}
