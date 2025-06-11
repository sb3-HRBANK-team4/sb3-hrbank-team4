package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.BackupLogNotFoundException;
import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.mapper.BackupLogMapper;
import com.fource.hrbank.repository.FileMetadataRepository;
import com.fource.hrbank.repository.backup.BackupLogRepository;
import com.fource.hrbank.repository.change.ChangeLogRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.service.backup.BackupService;
import com.fource.hrbank.service.storage.FileStorage;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
    private BackupService backupService;

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BackupLogMapper backupLogMapper;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileStorage fileStorage;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public FileStorage fileStorage() {
            return Mockito.mock(FileStorage.class);
        }
    }

    @Value("${spring.profiles.active}")
    private String profile;

    @BeforeEach
    void setUp() {
        // 테이블 ID 시퀀스 초기화
        if (!profile.equals("local")) {
            jdbcTemplate.execute("TRUNCATE TABLE tbl_file_metadata RESTART IDENTITY CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE tbl_backup_history RESTART IDENTITY CASCADE");
        }
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

    @Test
    void findLatestByStatus_정상조회_최근백업반환() {
        // given
        BackupLog oldLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(3600), Instant.now().minusSeconds(3590), BackupStatus.COMPLETED, null);
        BackupLog newLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.COMPLETED, null);
        List<BackupLog> logs = List.of(oldLog, newLog);
        backupLogRepository.saveAll(logs);

        // when
        BackupDto result = backupService.findLatestByStatus(BackupStatus.COMPLETED);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(newLog.getId());  // newLog가 반환되었는지 확인
        assertThat(result.status()).isEqualTo(BackupStatus.COMPLETED);
        assertThat(result.endedAt()).isEqualTo(newLog.getEndedAt());
    }

    @Test
    void findLatestByStatus_조회결과없음_예외발생() {
        // given
        BackupLog oldLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(3600), Instant.now().minusSeconds(3590), BackupStatus.COMPLETED, null);
        BackupLog newLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.COMPLETED, null);
        List<BackupLog> logs = List.of(oldLog, newLog);
        backupLogRepository.saveAll(logs);

        // when & then
        assertThatThrownBy(() -> backupService.findLatestByStatus(BackupStatus.SKIPPED))
                .isInstanceOf(BackupLogNotFoundException.class)
                .hasMessage(ResponseMessage.BACKUPLOG_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    void create_정상동작_백업필요() {
        // given
        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.COMPLETED, null);
        backupLogRepository.save(backupLog);

        Employee employee = new Employee(null, null, "김가", "a@email.com", "EMP-001", "주임", LocalDate.now(), EmployeeStatus.ACTIVE, Instant.now());
        employeeRepository.save(employee);

        ChangeLog changeLog = new ChangeLog("EMP-001",Instant.now(), "127.0.0.1", ChangeType.UPDATED, null, null);
        changeLogRepository.save(changeLog);

        // when
        BackupDto result = backupService.create("127.0.0.1");

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BackupStatus.IN_PROGRESS);
        assertThat(result.worker()).isEqualTo("127.0.0.1");
    }

    @Test
    void create_정상동작_백업필요없음() {
        // given
        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.COMPLETED, null);
        backupLogRepository.save(backupLog);

        Employee employee = new Employee(null, null, "김가", "a@email.com", "EMP-001", "주임", LocalDate.now(), EmployeeStatus.ACTIVE, Instant.now());
        employeeRepository.save(employee);

        ChangeLog changeLog = new ChangeLog("EMP-001", Instant.now().minusSeconds(3600), "127.0.0.1", ChangeType.UPDATED, null, null);
        changeLogRepository.save(changeLog);

        // when
        BackupDto result = backupService.create("127.0.0.1");

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BackupStatus.SKIPPED);
        assertThat(result.worker()).isEqualTo("127.0.0.1");
    }

    @Test
    void backup_정상동작_건너뜀상태반환됨() {
        // given
        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.SKIPPED, null);
        backupLogRepository.save(backupLog);
        BackupDto skipped = backupLogMapper.toDto(backupLog);

        // when
        BackupDto result = backupService.backup(skipped);

        // then
        assertThat(result).isEqualTo(skipped);
    }

    @Test
    void backup_정상동작_백업완료() {
        // given
        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.IN_PROGRESS, null);
        backupLogRepository.save(backupLog);
        BackupDto inProgressed = backupLogMapper.toDto(backupLog);

        // when
        BackupDto result = backupService.backup(inProgressed);

        // then
        assertThat(result).isNotEqualTo(inProgressed);
        assertThat(result.status()).isEqualTo(BackupStatus.COMPLETED);
    }

    @Test
    void backup_정상동작_백업실패() {
        // given
        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.IN_PROGRESS, null);
        backupLogRepository.save(backupLog);
        BackupDto backupDto = backupLogMapper.toDto(backupLog);

        Employee employee = new Employee(null, null, "김가", "a@email.com", "EMP-001", "주임", LocalDate.now(), EmployeeStatus.ACTIVE, Instant.now());
        employeeRepository.save(employee);

        // 첫번째 put() : 예외, 두번째는 성공
        when(fileStorage.put(any(),any()))
                .thenThrow(new RuntimeException("파일 저장 실패"))
                .thenReturn(backupDto.id());

        // when
        BackupDto result = backupService.backup(backupDto);

        // then
        assertThat(result.status()).isEqualTo(BackupStatus.FAILED);
    }

    @Test
    void backup_동작실패_예외발생() {
        // given
        BackupLog backupLog = new BackupLog("127.0.0.1", Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1790), BackupStatus.IN_PROGRESS, null);
        backupLogRepository.save(backupLog);
        BackupDto backupDto = backupLogMapper.toDto(backupLog);

        Employee employee = new Employee(null, null, "김가", "a@email.com", "EMP-001", "주임", LocalDate.now(), EmployeeStatus.ACTIVE, Instant.now());
        employeeRepository.save(employee);

        // 예외 발생
        when(fileStorage.put(any(),any()))
                .thenThrow(new RuntimeException("파일 저장 실패"))
                .thenThrow(new RuntimeException("에러 로그 저장 실패"));

        // when & then
        assertThatThrownBy(()-> backupService.backup(backupDto))
                .isInstanceOf(FileIOException.class)
                .hasMessage(ResponseMessage.FILE_SAVE_ERROR_MESSAGE);
    }

    @Test
    void batchBackup_정상동작_로직수행됨() {
        // when
        BackupDto backupDto = backupService.batchBackup();

        // then
        assertThat(backupDto.status()).isIn(BackupStatus.COMPLETED, BackupStatus.SKIPPED);
    }
}
