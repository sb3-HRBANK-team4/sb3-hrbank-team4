package com.fource.hrbank.service.backup;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "hrbank.batch.enabled", havingValue = "true")
public class BackupScheduler {

    private final BackupService backupService;

    @Scheduled(fixedDelayString = "${hrbank.batch.time}")
    public void run() {
        backupService.batchBackup();
    }
}