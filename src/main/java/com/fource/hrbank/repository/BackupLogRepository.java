package com.fource.hrbank.repository;

import com.fource.hrbank.domain.BackupLog;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupLogRepository extends JpaRepository<BackupLog, Long>,
    BackupLogCustomRepository {

}
