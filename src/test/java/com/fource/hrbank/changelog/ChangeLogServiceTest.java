package com.fource.hrbank.changelog;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fource.hrbank.Sb3HrbankFourceApplication;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.changelog.ChangeLogCreateRequestDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.repository.ChangeLogRepository;
import com.fource.hrbank.repository.EmployeeRepository;
import com.fource.hrbank.service.changelog.ChangeLogService;
import com.fource.hrbank.service.storage.FileStorage;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ChangeLogServiceTest {

    @MockBean
    private FileStorage fileStorage;

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
            new Date(),
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
            new Date(),
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
}