package com.fource.hrbank.service.backup;

import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.dto.backup.CursorPageResponseBackupDto;

import java.time.Instant;

public interface BackupService {

    CursorPageResponseBackupDto findAll(
        String worker, BackupStatus status,
        Instant startedAtFrom, Instant startedAtTo,
        Long idAfter, String cursor, int size,
        String sortField, String sortDirection
    );

    BackupDto findLatestByStatus(BackupStatus status);

    BackupDto create(String ipAdress);

    BackupDto backup(BackupDto backupDto);
}