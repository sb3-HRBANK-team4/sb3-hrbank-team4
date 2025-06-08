package com.fource.hrbank.repository;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.dto.backup.BackupDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface BackupLogCustomRepository {

    List<BackupLog> findByCursorCondition(
            String worker, Instant startedAtFrom,
            Instant startedAtTo, BackupStatus status,
            Long idAfter, String cusrosr,
            String sortField, String sortDirection,
            Pageable pageable);

    Long countByCondition(String worker, Instant startedAtFrom, Instant startedAtTo, BackupStatus status);

    Optional<BackupLog> findLatestByStatus(BackupStatus status);

    Optional<BackupLog> findLatest();
}



