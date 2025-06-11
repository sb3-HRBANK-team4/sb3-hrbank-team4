package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.exception.DuplicateEmailException;
import com.fource.hrbank.exception.EmployeeNotFoundException;
import com.fource.hrbank.repository.change.ChangeLogRepository;
import com.fource.hrbank.repository.department.DepartmentRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.service.dashboard.DashboardService;
import com.fource.hrbank.service.employee.EmployeeService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.profiles.active}")
    private String profile;

    @BeforeEach
    void setUp() {
        if (!profile.equals("local")) {
            //시퀀스 초기화
            jdbcTemplate.execute(
                    "TRUNCATE TABLE tbl_change_detail, tbl_change_log, tbl_employees RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void findById_정상조회() {
        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        Employee emp1 = new Employee(null, department, "김가", "a@email.com", "EMP-2025-", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());
        Employee savedEmp1 = employeeRepository.save(emp1);

        EmployeeDto result = employeeService.findById(savedEmp1.getId());

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("김가");
    }


    @Test
    void findAll_검색조건없음_커서페이지네이션_정상작동() {
        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        Employee emp1 = new Employee(null, department, "가", "a@email.com", "EMP-2025-", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());
        Employee emp2 = new Employee(null, department, "나", "b@email.com", "EMP-2025-", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());
        Employee emp3 = new Employee(null, department, "다", "c@email.com", "EMP-2025-", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());

        employeeRepository.saveAll(List.of(emp1, emp2, emp3));

        // 검색 및 정렬 조건 없음(기본 정렬: 이름 오름차순)
        String nameOrEmail = null;
        String employeeNumber = null;
        String departmentName = department.getName();
        String position = null;
        EmployeeStatus status = null;
        String sortField = "name";
        String sortDirection = "asc";
        String cursor = null;
        Long idAfter = null;
        int size = 2;
        LocalDate hireDateFrom = LocalDate.of(2023, 1, 1);
        LocalDate hireDateTo = LocalDate.of(2025, 1, 1);


        // when
        CursorPageResponse<EmployeeDto> result = employeeService.findAll(
            nameOrEmail, employeeNumber, departmentName, position,  status, hireDateFrom, hireDateTo,
            sortField, sortDirection, cursor, idAfter, size
        );

        // then
        assertThat(result.content().size()).isEqualTo(2);
        assertThat(result.content().get(0).name()).startsWith("가"); // 이름순 정렬 확인
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotNull();
        assertThat(result.nextIdAfter()).isNotNull();
    }

    @Test
    void findAll_이름내림차순정렬_확인() {

        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        employeeRepository.saveAll(List.of(
            new Employee(null, department, "가", "a@email.com", "EMP-001", "주임",
                LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()),
            new Employee(null, department, "나", "b@email.com", "EMP-002", "사원",
                LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()),
            new Employee(null, department, "다", "c@email.com", "EMP-003", "과장",
                LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now())
        ));

        LocalDate hireDateFrom = LocalDate.of(2023, 1, 1);
        LocalDate hireDateTo = LocalDate.of(2025, 1, 1);

        // when
        CursorPageResponse<EmployeeDto> result = employeeService.findAll(
            null, null, department.getName(), null, null,
            hireDateFrom, hireDateTo,
            "name", "desc",
            null, null, 10
        );

        // then
        List<String> names = result.content().stream()
            .map(EmployeeDto::name)
            .toList();

        assertThat(names).containsExactly("다", "나", "가");
    }

    @Test
    void create_직원생성_프로필이미지없음_확인() {

        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        EmployeeCreateRequest request = new EmployeeCreateRequest(
            "조현아",
            "hyun@gmail.com",
            department.getId(),
            "주임",
            LocalDate.of(2025, 6, 2),
            null // memo
        );

        // when
        EmployeeDto employeeDto = employeeService.create(request, Optional.empty());

        // then
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.name()).isEqualTo("조현아");
        assertThat(employeeDto.email()).isEqualTo("hyun@gmail.com");
        assertThat(employeeDto.employeeNumber()).startsWith("EMP-2025-");
        assertThat(employeeDto.departmentId()).isEqualTo(department.getId());
        assertThat(employeeDto.departmentName()).isEqualTo("백엔드 개발팀");
        assertThat(employeeDto.position()).isEqualTo("주임");
        assertThat(employeeDto.hireDate()).isEqualTo(LocalDate.of(2025, 6, 2));
        assertThat(employeeDto.status()).isEqualTo(EmployeeStatus.ACTIVE);
        assertThat(employeeDto.profileImageId()).isNull();
    }

    @Test
    void create_직원생성_프로필이미지있음_확인() {

        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        byte[] fileBytes = "dummy image data".getBytes();
        MockMultipartFile profileImage = new MockMultipartFile(
            "profile", // part 이름
            "profile.png", // 파일 이름
            MediaType.IMAGE_PNG_VALUE, // 컨텐츠 타입
            fileBytes
        );

        EmployeeCreateRequest request = new EmployeeCreateRequest(
            "조현아",
            "hyun@gmail.com",
            department.getId(),
            "사원",
            LocalDate.of(2025, 6, 2),
            null // memo
        );

        // when
        EmployeeDto employeeDto = employeeService.create(request, Optional.of(profileImage));

        // then
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.name()).isEqualTo("조현아");
        assertThat(employeeDto.email()).isEqualTo("hyun@gmail.com");
        assertThat(employeeDto.employeeNumber()).startsWith("EMP-2025-");
        assertThat(employeeDto.departmentId()).isEqualTo(department.getId());
        assertThat(employeeDto.departmentName()).isEqualTo("백엔드 개발팀");
        assertThat(employeeDto.position()).isEqualTo("사원");
        assertThat(employeeDto.hireDate()).isEqualTo(LocalDate.of(2025, 6, 2));
        assertThat(employeeDto.status()).isEqualTo(EmployeeStatus.ACTIVE);
        assertThat(employeeDto.profileImageId()).isNotNull();
    }

    @Test
    void update_직원정보수정_이름_이메일_부서_입사일_변경_확인() {
        // given
        Department department = departmentRepository.save(
            new Department("개발팀", "소프트웨어 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        Department newDepartment = departmentRepository.save(
            new Department("기획팀", "서비스 기획을 담당합니다.", LocalDate.now(), Instant.now())
        );

        // 기존 직원 생성
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRemoteAddr("127.0.0.1");

        EmployeeCreateRequest createRequest = new EmployeeCreateRequest(
            "김철수",
            "kimcs@example.com",
            department.getId(),
            "사원",
            LocalDate.of(2025, 1, 1),
            "신규 직원 등록"
        );

        EmployeeDto createdEmployee = employeeService.create(createRequest, Optional.empty());

        // 수정할 정보
        EmployeeUpdateRequest updateRequest = new EmployeeUpdateRequest(
            "김철수_수정", // 이름 변경
            "kimcs_updated@example.com", // 이메일 변경
            newDepartment.getId(), // 부서 변경
            "대리", // 직함 변경
            LocalDate.of(2025, 2, 1), // 입사일 변경
            EmployeeStatus.ACTIVE,
            "직원 정보 일괄 수정"
        );

        // when
        EmployeeDto updatedEmployee = employeeService.update(
            createdEmployee.id(), updateRequest, Optional.empty());

        // then
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.id()).isEqualTo(createdEmployee.id());
        assertThat(updatedEmployee.name()).isEqualTo("김철수_수정");
        assertThat(updatedEmployee.email()).isEqualTo("kimcs_updated@example.com");
        assertThat(updatedEmployee.departmentId()).isEqualTo(newDepartment.getId());
        assertThat(updatedEmployee.departmentName()).isEqualTo("기획팀");
        assertThat(updatedEmployee.position()).isEqualTo("대리");
        assertThat(updatedEmployee.hireDate()).isEqualTo(LocalDate.of(2025, 2, 1));
        assertThat(updatedEmployee.status()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void update_이메일중복_예외발생() {
        // given
        Department department = departmentRepository.save(
            new Department("개발팀", "소프트웨어 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        // 첫 번째 직원 생성
        EmployeeCreateRequest createRequest1 = new EmployeeCreateRequest(
            "김철수", "kimcs@example.com", department.getId(), "사원", LocalDate.of(2025, 1, 1), "신규 등록"
        );
        employeeService.create(createRequest1, Optional.empty());

        // 두 번째 직원 생성
        EmployeeCreateRequest createRequest2 = new EmployeeCreateRequest(
            "이영희", "leeyh@example.com", department.getId(), "사원", LocalDate.of(2025, 1, 2), "신규 등록"
        );
        EmployeeDto secondEmployee = employeeService.create(createRequest2, Optional.empty());

        // 두 번째 직원의 이메일을 첫 번째 직원과 동일하게 변경 시도
        EmployeeUpdateRequest updateRequest = new EmployeeUpdateRequest(
            "이영희", "kimcs@example.com", // 중복 이메일
            department.getId(), "사원", LocalDate.of(2025, 1, 2), EmployeeStatus.ACTIVE, "이메일 변경"
        );

        // when & then
        assertThatThrownBy(() ->
            employeeService.update(secondEmployee.id(), updateRequest, Optional.empty())
        ).isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("중복된 이메일입니다.");
    }

    @Test
    void update_존재하지않는직원_예외발생() {
        // given
        Department department = departmentRepository.save(
            new Department("개발팀", "소프트웨어 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRemoteAddr("127.0.0.1");

        EmployeeUpdateRequest updateRequest = new EmployeeUpdateRequest(
            "김철수", "kimcs@example.com", department.getId(), "사원",
            LocalDate.of(2025, 1, 1), EmployeeStatus.ACTIVE, "수정 시도"
        );

        // when & then
        assertThatThrownBy(() ->
            employeeService.update(999L, updateRequest, Optional.empty())
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void getEmployeeDistribution_부서기준_비율확인() {
        // given
        Department dept1 = departmentRepository.save(new Department("백엔드", "백엔드팀", LocalDate.now(), Instant.now()));
        Department dept2 = departmentRepository.save(new Department("프론트엔드", "프론트엔드팀", LocalDate.now(), Instant.now()));
        Department dept3 = departmentRepository.save(new Department("기획", "기획팀", LocalDate.now(), Instant.now()));
        Department dept4 = departmentRepository.save(new Department("디자인", "디자인팀", LocalDate.now(), Instant.now()));

        employeeRepository.save(new Employee(null, dept1, "A", "a1@email.com", "EMP-001", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()));
        employeeRepository.save(new Employee(null, dept2, "B", "b1@email.com", "EMP-002", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()));
        employeeRepository.save(new Employee(null, dept3, "C", "c1@email.com", "EMP-003", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()));
        employeeRepository.save(new Employee(null, dept4, "D", "d1@email.com", "EMP-004", "주임",
            LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()));

        // when
        List<EmployeeDistributionDto> result = dashboardService.getEmployeeDistribution("department", EmployeeStatus.ACTIVE);

        // then
        assertThat(result).hasSize(4);
        for (EmployeeDistributionDto dto : result) {
            assertThat(dto.percentage()).isEqualTo(25.0); // 4명 → 25%씩 분포
        }
    }

    @Test
    void getEmployeeTrend_월별직원수추이_정상작동() {
        // given
        Department dept = departmentRepository.save(
            new Department("개발팀", "개발부서", LocalDate.now(), Instant.now())
        );

        // 월별 고르게 입사자를 배치 (2025-01 ~ 2025-04)
        employeeRepository.save(new Employee(null, dept, "A", "a@email.com", "EMP-001", "주임",
            LocalDate.of(2025, 1, 10), EmployeeStatus.ACTIVE, Instant.now()));
        employeeRepository.save(new Employee(null, dept, "B", "b@email.com", "EMP-002", "사원",
            LocalDate.of(2025, 2, 5), EmployeeStatus.ACTIVE, Instant.now()));
        employeeRepository.save(new Employee(null, dept, "C", "c@email.com", "EMP-003", "과장",
            LocalDate.of(2025, 3, 1), EmployeeStatus.ACTIVE, Instant.now()));
        employeeRepository.save(new Employee(null, dept, "D", "d@email.com", "EMP-004", "대리",
            LocalDate.of(2025, 4, 25), EmployeeStatus.ACTIVE, Instant.now()));

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 4, 30);

        // when
        var trendList = dashboardService.getEmployeeTrend(from, to, "month");

        // then
        assertThat(trendList).hasSize(4);

        assertThat(trendList.get(0).date()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(trendList.get(0).count()).isEqualTo(1);
        assertThat(trendList.get(0).change()).isEqualTo(0); // 첫 달이므로 비교 대상 없음
        assertThat(trendList.get(0).changeRate()).isEqualTo(0.0);

        assertThat(trendList.get(1).count()).isEqualTo(2); // 1→2
        assertThat(trendList.get(1).change()).isEqualTo(1);
        assertThat(trendList.get(1).changeRate()).isEqualTo(100.0);

        assertThat(trendList.get(2).count()).isEqualTo(3);
        assertThat(trendList.get(3).count()).isEqualTo(4);
    }

    @Test
    void deleteById_정상삭제() {
        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", LocalDate.now(), Instant.now())
        );

        Employee employee = new Employee(null, department, "조현아", "hyun@gmail.com", "emp-2025-000", "사원", LocalDate.of(2025, 6, 2), EmployeeStatus.ACTIVE, Instant.now());
        employeeRepository.save(employee);

        // when
        employeeService.deleteById(employee.getId());

        // then
        List<ChangeLog> logs = changeLogRepository.findAll();
        assertThat(logs).hasSize(1);
        ChangeLog log = logs.get(0);
        assertThat(log.getType()).isEqualTo(ChangeType.DELETED);
        assertThat(log.getMemo()).isEqualTo("직원 삭제");
        assertThat(log.getChangedIp()).isEqualTo("127.0.0.1");

    }

}