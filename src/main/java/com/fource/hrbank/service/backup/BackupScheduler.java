package com.fource.hrbank.service.backup;

import com.fource.hrbank.dto.backup.BackupDto;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "hrbank.batch.enabled", havingValue = "true", matchIfMissing = false)
public class BackupScheduler {

    @Value("${hrbank.batch.time}")
    private long batchTime;

    private final BackupService backupService;

    @PostConstruct
    public void init() {
        log.info("생성됨 - 해시코드: {}, 클래스: {}, 스레드: {}",
                System.identityHashCode(this), this.getClass(), Thread.currentThread().getName());
    }

    @Scheduled(fixedDelayString = "${hrbank.batch.time}")
    public void run() {
        try {
            log.info("배치 백업 실행됨 - 현재 시각: {}, 배치 주기: {}ms", LocalDateTime.now(), batchTime);
            log.info("실행되고 있는 스레드 : " + Thread.currentThread().getName());
            backupService.batchBackup();
        } catch(Exception e) {
            log.error("스케줄러 작업 중 오류 발생", e);
        }
    }
}