package com.fource.hrbank.service;

import static jdk.dynalink.linker.support.Guards.isNotNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.changelog.ChangeLogCreateRequestDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.repository.ChangeLogRepository;
import com.fource.hrbank.repository.EmployeeRepository;
import com.fource.hrbank.service.changelog.ChangeLogService;
import com.fource.hrbank.service.storage.FileStorage;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
public class ChangeLogServiceTest {

    @Autowired
    private FileStorage fileStorage;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FileStorage fileStorage() {
            return Mockito.mock(FileStorage.class);
        }
    }

    @Autowired
    private ChangeLogService changeLogService;

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테이블 삭제
        changeLogRepository.deleteAll();

        // 시퀀스를 "테이블의 MAX(id) + 1"로 세팅
        jdbcTemplate.execute("""
            SELECT setval('tbl_change_log_id_seq', 
                          COALESCE((SELECT MAX(id) FROM tbl_change_log), 0) + 1,
                          false)
        """);
    }

    @Test
    @DisplayName("정보 수정 이력 단건 조회 - 성공")
    void 정보수정이력_단건조회_성공() {
        // given
        Employee employee = new Employee(
            null,
            null,
            "김단건",
            "single@test.com",
            "01000000000",
            "테스트개발자",
            LocalDate.of(2024, 1, 1),
            EmployeeStatus.ACTIVE,
            Instant.now()
        );
        employeeRepository.save(employee);

        ChangeLog entity = new ChangeLog(
            employee,
            Instant.now(),
            "127.0.0.1",
            ChangeType.CREATED,
            "단건조회 테스트",
            null
        );

        ChangeLog saved = changeLogRepository.save(entity);

        // when
        ChangeLogDto result = changeLogService.findById(saved.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getMemo()).isEqualTo("단건조회 테스트");
    }

    @Test
    @DisplayName("정보 수정 이력 생성 - 성공")
    void 정보수정이력_생성_성공() {
        Employee employee = new Employee(
            null,
            null,
            "김코딩",
            "test@test.com",
            "0123456789",
            "개발자",
            LocalDate.of(2024, 1, 1),
            EmployeeStatus.ACTIVE,
            Instant.now()
        );
        employeeRepository.save(employee);

        ChangeLogCreateRequestDto request = ChangeLogCreateRequestDto.builder()
            .employeeNumber(employee.getEmployeeNumber())
            .memo("테스트 메모")
            .ipAddress("127.0.0.1")
            .type(ChangeType.CREATED)
            .build();

        ChangeLogDto result = changeLogService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getMemo()).isEqualTo("테스트 메모");
    }

    @Test
    @DisplayName("정보 수정 이력 목록 조회 - changedAt 기준 내림차순 정렬만 검증")
    void 정보수정이력_정렬기능_CHANGED_AT_DESC() {
        // given
        Employee employee = new Employee(
            null,
            null,
            "김정렬",
            "sort@test.com",
            "01099990000",
            "정렬개발자",
            LocalDate.of(2024, 5, 1),
            EmployeeStatus.ACTIVE,
            Instant.now()
        );
        employeeRepository.save(employee);

        ChangeLog changeLog1 = new ChangeLog(
            employee,
            Instant.now().minusSeconds(60),
            "100.10.1.1",
            ChangeType.UPDATED,
            "메모1",
            null
        );
        ChangeLog changeLog2 = new ChangeLog(
            employee,
            Instant.now().minusSeconds(30),
            "100.10.1.2",
            ChangeType.UPDATED,
            "메모2",
            null
        );
        ChangeLog changeLog3 = new ChangeLog(
            employee,
            Instant.now(),
            "100.10.1.3",
            ChangeType.UPDATED,
            "메모3",
            null
        );

        changeLogRepository.saveAll(List.of(changeLog1, changeLog2, changeLog3));
        System.out.println("저장된 이력 수: " + changeLogRepository.count());

        // when
        CursorPageResponseChangeLogDto result = changeLogService.findAll(
            null, null, null, null, null, null,
            10, "changedAt", "DESC"
        );

        // then
        List<ChangeLogDto> list = result.content();
        System.out.println("조회된 이력 수: " + list.size());

        assertThat(list).isNotNull();
        assertThat(list).isSortedAccordingTo(
            Comparator.comparing(ChangeLogDto::getChangedAt).reversed()
        );
    }
}