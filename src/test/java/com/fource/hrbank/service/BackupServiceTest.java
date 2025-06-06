package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.repository.BackupLogRepository;
import com.fource.hrbank.repository.FileMetadataRepository;
import com.fource.hrbank.service.backup.BackupService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class BackupServiceTest {

    @Autowired
    private BackupLogRepository backupLogRepository;

    @Autowired
    private BackupService backupService;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테이블 삭제
        fileMetadataRepository.deleteAll();
        backupLogRepository.deleteAll();

        // 시퀀스를 "테이블의 MAX(id) + 1"로 세팅
        jdbcTemplate.execute("""
            SELECT setval('tbl_file_metadata_id_seq', 
                          COALESCE((SELECT MAX(id) FROM tbl_file_metadata), 0) + 1,
                          false)
        """);
        jdbcTemplate.execute("""
            SELECT setval('tbl_backup_history_id_seq', 
                          COALESCE((SELECT MAX(id) FROM tbl_backup_history), 0) + 1,
                          false)
        """);
    }

    @Test
    void findAll_정상조회_fileId존재() {
        // given
        FileMetadata fileMetadata = new FileMetadata("profile_1.jpg", "image/jpeg", 39790L);
        fileMetadataRepository.save(fileMetadata);

        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now(),Instant.now(), BackupStatus.COMPLETED,fileMetadata);
        backupLogRepository.save(backupLog);

        // when
        List<BackupDto> result = backupService.findAll();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).fileId()).isNotNull();
        assertThat(result.get(0).status()).isEqualTo(BackupStatus.COMPLETED);
    }

    @Test
    void findAll_정상조회_fileIdNull() {
        // given
        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now(),Instant.now(), BackupStatus.SKIPPED,null);
        backupLogRepository.save(backupLog);

        // when
        List<BackupDto> result = backupService.findAll();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).fileId()).isNull();
        assertThat(result.get(0).status()).isEqualTo(BackupStatus.SKIPPED);
    }
}
