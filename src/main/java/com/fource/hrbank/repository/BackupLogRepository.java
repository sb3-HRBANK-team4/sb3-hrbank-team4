package com.fource.hrbank.repository;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.dto.backup.BackupDto;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupLogRepository extends JpaRepository<BackupLog, Long>, BackupLogCustomRepository {

}
