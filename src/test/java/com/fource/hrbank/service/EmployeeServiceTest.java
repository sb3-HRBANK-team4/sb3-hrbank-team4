package com.fource.hrbank.service;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.repository.DepartmentRepository;
import com.fource.hrbank.repository.EmployeeRepository;
import com.fource.hrbank.service.employee.EmployeeService;
import com.fource.hrbank.service.storage.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테이블 삭제
        employeeRepository.deleteAll();

        // 시퀀스 초기화
        jdbcTemplate.execute("""
                SELECT setval('tbl_employees_id_seq',
                              COALESCE((SELECT MAX(id) FROM tbl_employees), 0) + 1,
                              false)
            """);
    }

    @Test
    void findById_정상조회() {
        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", Instant.now(), Instant.now())
        );

        Employee emp1 = new Employee(null, department, "김가", "a@email.com", "EMP-2025-", "주임", LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());
        Employee savedEmp1 = employeeRepository.save(emp1);

        EmployeeDto result = employeeService.findById(savedEmp1.getId());

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("김가");
    }


    @Test
    void findAll_검색조건없음_커서페이지네이션_정상작동() {
        Department department = departmentRepository.save(
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", Instant.now(), Instant.now())
        );

        Employee emp1 = new Employee(null, department, "가", "a@email.com", "EMP-2025-", "주임", LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());
        Employee emp2 = new Employee(null, department, "나", "b@email.com", "EMP-2025-", "주임", LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());
        Employee emp3 = new Employee(null, department, "다", "c@email.com", "EMP-2025-", "주임", LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now());

        employeeRepository.saveAll(List.of(emp1, emp2, emp3));

        // 검색 및 정렬 조건 없음(기본 정렬: 이름 오름차순)
        String nameOrEmail = null;
        String departmentName = department.getName();
        String position = null;
        EmployeeStatus status = null;
        String sortField = "name";
        String sortDirection = "asc";
        String cursor = null;
        Long idAfter = null;
        int size = 2;

        // when
        CursorPageResponseEmployeeDto result = employeeService.findAll(
            nameOrEmail, departmentName, position, status,
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
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", Instant.now(), Instant.now())
        );

        employeeRepository.saveAll(List.of(
            new Employee(null, department, "가", "a@email.com", "EMP-001", "주임", LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()),
            new Employee(null, department, "나", "b@email.com", "EMP-002", "사원", LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now()),
            new Employee(null, department, "다", "c@email.com", "EMP-003", "과장", LocalDate.of(2023, 1, 1), EmployeeStatus.ACTIVE, Instant.now())
        ));

        // when
        CursorPageResponseEmployeeDto result = employeeService.findAll(
            null, department.getName(), null, null,
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
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", Instant.now(), Instant.now())
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
            new Department("백엔드 개발팀", "서버 개발을 담당합니다.", Instant.now(), Instant.now())
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
}
