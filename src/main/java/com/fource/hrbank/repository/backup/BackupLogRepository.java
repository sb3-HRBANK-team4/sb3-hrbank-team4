package com.fource.hrbank.repository.backup;

import com.fource.hrbank.domain.BackupLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupLogRepository extends JpaRepository<BackupLog, Long>,
        BackupLogCustomRepository {

}
