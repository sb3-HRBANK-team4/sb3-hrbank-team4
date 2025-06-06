package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.dto.backup.BackupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackupLogMapper {

    @Mapping(target = "fileId", expression = "java(backupLog.getBackupFile() != null ? backupLog.getBackupFile().getId() : null)")
    BackupDto toDto(BackupLog backupLog);
}
