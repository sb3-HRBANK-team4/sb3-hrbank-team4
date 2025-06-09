package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.repository.ChangeDetailRepository;
import com.fource.hrbank.repository.ChangeLogRepository;
import com.fource.hrbank.repository.DepartmentRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.service.changelog.ChangeLogService;
import com.fource.hrbank.service.employee.EmployeeService;
import com.fource.hrbank.service.storage.FileStorage;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private DepartmentRepository departmentRepository;

    @Autowired
    private ChangeDetailRepository changeDetailRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute(
            "TRUNCATE TABLE tbl_change_detail, tbl_change_log, tbl_employees, tbl_department RESTART IDENTITY CASCADE");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
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
        Department newDepartment = departmentRepository.save(
            new Department("마케팅팀", "마케팅 업무", LocalDate.of(2025, 1, 1), Instant.now())
        );

        Employee employee = new Employee(
            null,
            newDepartment,
            "김코딩",
            "test@test.com",
            "0123456789",
            "개발자",
            LocalDate.of(2024, 1, 1),
            EmployeeStatus.ACTIVE,
            Instant.now()
        );
        employeeRepository.save(employee);

        EmployeeUpdateRequest request = new EmployeeUpdateRequest(
            "변경된 이름_김코딩",
            employee.getEmail(),
            newDepartment.getId(),
            employee.getPosition(),
            employee.getHireDate(),
            employee.getStatus(),
            "이름 변경"
        );

        // when
        employeeService.update(employee.getId(), request, Optional.empty());

        // then
        List<ChangeLog> changeLogs = changeLogRepository.findAll();
        ChangeLog updateLog = changeLogs.stream()
            .filter(log -> log.getType() == ChangeType.UPDATED)
            .findFirst()
            .orElse(null);

        assertThat(updateLog).isNotNull();

        List<ChangeDetail> details = changeDetailRepository.findByChangeLog(updateLog);
        assertThat(details.size()).isEqualTo(1);

        ChangeDetail deptChange = details.get(0);
        assertThat(deptChange.getFieldName()).isEqualTo("name");
    }
}