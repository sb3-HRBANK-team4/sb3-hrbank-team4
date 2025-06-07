package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.dto.backup.BackupDto;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-07T15:29:57+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.jar, environment: Java 17.0.14 (Homebrew)"
)
@Component
public class BackupLogMapperImpl implements BackupLogMapper {

    @Override
    public BackupDto toDto(BackupLog backupLog) {
        if ( backupLog == null ) {
            return null;
        }

        Long id = null;
        String worker = null;
        Instant startedAt = null;
        Instant endedAt = null;
        BackupStatus status = null;

        id = backupLog.getId();
        worker = backupLog.getWorker();
        startedAt = backupLog.getStartedAt();
        endedAt = backupLog.getEndedAt();
        status = backupLog.getStatus();

        Long fileId = backupLog.getBackupFile() != null ? backupLog.getBackupFile().getId() : null;

        BackupDto backupDto = new BackupDto( id, worker, startedAt, endedAt, status, fileId );

        return backupDto;
    }
}
