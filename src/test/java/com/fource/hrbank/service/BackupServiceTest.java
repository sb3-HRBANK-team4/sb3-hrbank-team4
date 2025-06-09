package com.fource.hrbank.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.repository.BackupLogRepository;
import com.fource.hrbank.repository.FileMetadataRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BackupServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BackupLogRepository backupLogRepository;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테이블 ID 시퀀스 초기화
        jdbcTemplate.execute("TRUNCATE TABLE tbl_file_metadata RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE tbl_backup_history RESTART IDENTITY CASCADE");
    }

    @Test
    void findAll_정상조회() throws Exception {
        // given
        Instant startedAt = Instant.parse("2024-01-01T10:00:00Z");
        Instant endedAt = Instant.parse("2024-01-01T10:30:00Z");

        BackupLog log1 = new BackupLog("182.216.32.93", startedAt, endedAt, BackupStatus.COMPLETED,
            null);
        BackupLog log2 = new BackupLog("125.247.249.56", startedAt.plusSeconds(3600),
            endedAt.plusSeconds(3600), BackupStatus.COMPLETED, null);
        BackupLog log3 = new BackupLog("14.63.67.157", startedAt.plusSeconds(7200),
            endedAt.plusSeconds(7200), BackupStatus.COMPLETED, null);

        backupLogRepository.saveAll(List.of(log1, log2, log3));

        // when & then
        mockMvc.perform(get("/api/backups")
                .param("status", "COMPLETED")
                .param("startedAtFrom", "2024-01-01T00:00:00Z")
                .param("startedAtTo", "2024-01-02T00:00:00Z")
                .param("sortField", "startedAt")
                .param("sortDirection", "ASC")
                .param("size", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(3))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.size").value(3));
    }
}
