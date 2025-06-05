package com.fource.hrbank.service.backup;

import com.fource.hrbank.dto.backup.BackupDto;
import java.util.List;

public interface BackupService {

    List<BackupDto> findAll();
}
