package com.fource.hrbank.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.repository.change.ChangeDetailRepository;
import com.fource.hrbank.repository.change.ChangeLogRepository;
import com.fource.hrbank.repository.department.DepartmentRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.service.changelog.ChangeLogService;
import com.fource.hrbank.service.employee.EmployeeService;
import com.fource.hrbank.service.storage.FileStorage;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
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
    @DisplayName("정보 수정 이력 상세 조회 - 성공")
    void 정보수정이력_상세조회_성공() {
        Department department = departmentRepository.save(
            new Department("마케팅팀", "마케팅 업무", LocalDate.of(2025, 1, 1), Instant.now())
        );

        // given
        Employee employee = new Employee(
            null,
            department,
            "김단건",
            "single@test.com",
            "01000000000",
            "테스트개발자",
            LocalDate.of(2024, 1, 1),
            EmployeeStatus.ACTIVE,
            Instant.now(),
            false
        );
        employeeRepository.save(employee);

        ChangeLog entity = new ChangeLog(
            employee,
            employee.getEmployeeNumber(),
            Instant.now(),
            "127.0.0.1",
            ChangeType.CREATED,
            "단건조회 테스트",
            null
        );
        ChangeLog saved = changeLogRepository.save(entity);

        ChangeDetail detail = new ChangeDetail();
        detail.setChangeLog(entity);
        detail.setPropertyName("이름");
        detail.setBefore("김단건");
        detail.setAfter("김단순");

        changeDetailRepository.save(detail);

        // when
        List<DiffsDto> result = changeLogService.findDiffs(saved.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPropertyName()).isEqualTo("이름");
        assertThat(result.get(0).getBefore()).isEqualTo("김단건");
        assertThat(result.get(0).getAfter()).isEqualTo("김단순");
    }

    @Test
    @DisplayName("정보 수정 이력 생성 - 성공")
    void 정보수정이력_생성_성공() {
        Department department = departmentRepository.save(
            new Department("마케팅팀", "마케팅 업무", LocalDate.of(2025, 1, 1), Instant.now())
        );

        Employee employee = new Employee(
            null,
            department,
            "김코딩",
            "test@test.com",
            "0123456789",
            "개발자",
            LocalDate.of(2024, 1, 1),
            EmployeeStatus.ACTIVE,
            Instant.now(),
            false
        );
        employeeRepository.save(employee);

        EmployeeUpdateRequest request = new EmployeeUpdateRequest(
            "변경된 이름_김코딩",
            employee.getEmail(),
            department.getId(),
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
        assertThat(deptChange.getPropertyName()).isEqualTo("이름");
    }

    @Test
    @DisplayName("정보 수정 이력 목록 조회 - changedAt 기준 내림차순 정렬만 검증")
    void 정보수정이력_정렬기능_CHANGED_AT_DESC() {
        Department department = departmentRepository.save(
            new Department("마케팅팀", "마케팅 업무", LocalDate.of(2025, 1, 1), Instant.now())
        );

        // given
        Employee employee = new Employee(
            null,
            department,
            "김정렬",
            "sort@test.com",
            "01099990000",
            "정렬개발자",
            LocalDate.of(2024, 5, 1),
            EmployeeStatus.ACTIVE,
            Instant.now(),
            false
        );
        employeeRepository.save(employee);

        ChangeLog changeLog1 = new ChangeLog(
            employee,
            employee.getEmployeeNumber(),
            Instant.now().minusSeconds(60),
            "100.10.1.1",
            ChangeType.UPDATED,
            "메모1",
            null
        );
        ChangeLog changeLog2 = new ChangeLog(
            employee,
            employee.getEmployeeNumber(),
            Instant.now().minusSeconds(30),
            "100.10.1.2",
            ChangeType.UPDATED,
            "메모2",
            null
        );
        ChangeLog changeLog3 = new ChangeLog(
            employee,
            employee.getEmployeeNumber(),
            Instant.now(),
            "100.10.1.3",
            ChangeType.UPDATED,
            "메모3",
            null
        );

        changeLogRepository.saveAll(List.of(changeLog1, changeLog2, changeLog3));
        System.out.println("저장된 이력 수: " + changeLogRepository.count());

        // when
        Instant atFrom = Instant.now().minusSeconds(120); // 이전 시점
        Instant atTo = Instant.now().plusSeconds(10);     // 현재 이후 시점

        CursorPageResponse<ChangeLogDto> result = changeLogService.getAllChangeLogs(
            null, null, null, null, null, null,
            10, "changedAt", "DESC", atFrom, atTo
        );

        // then
        List<ChangeLogDto> list = result.content();
        System.out.println("조회된 이력 수: " + list.size());

        assertThat(list).isNotNull();
        assertThat(list).isSortedAccordingTo(
            Comparator.comparing(ChangeLogDto::getAt).reversed()
        );
    }
}