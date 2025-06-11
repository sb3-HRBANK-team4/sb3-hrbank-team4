package com.fource.hrbank.service;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.department.DepartmentDto;
import com.fource.hrbank.dto.department.DepartmentUpdateRequest;
import com.fource.hrbank.exception.DepartmentDeleteException;
import com.fource.hrbank.repository.department.DepartmentRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.service.department.DepartmentService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Value("${spring.profiles.active}")
    private String profile;

    @BeforeEach
    void setUp() {
        if (!profile.equals("local")) {
            // 테이블 삭제
            employeeRepository.deleteAll();
            departmentRepository.deleteAll();

            // 시퀀스를 "테이블의 MAX(id) + 1"로 세팅
            jdbcTemplate.execute("""
                SELECT setval('tbl_department_id_seq', 
                              COALESCE((SELECT MAX(id) FROM tbl_department), 0) + 1,
                              false)
            """);
            jdbcTemplate.execute("""
                SELECT setval('tbl_employees_id_seq', 
                              COALESCE((SELECT MAX(id) FROM tbl_employees), 0) + 1,
                              false)
            """);
        }
    }

    @Test
    void findAll_검색조건_없음_커서페이지네이션() {
        // given
        Department department1 = new Department("경리A", "자산 관리 A", LocalDate.now(), Instant.now());
        Department department2 = new Department("경리B", "자산 관리 B", LocalDate.now(), Instant.now());
        Department department3 = new Department("경리C", "자산 관리 C", LocalDate.now(), Instant.now());

        departmentRepository.saveAll(List.of(department1, department2, department3));

        String nameOrDescription = null;
        Long idAfter = null;
        String cursor = null;
        int size = 2;
        String sortField = "name";
        String sortDirection = "asc";

        // when
        CursorPageResponse<DepartmentDto> result = departmentService.findAll(
            nameOrDescription, idAfter, cursor, size, sortField, sortDirection);

        // then
        assertThat(result.content().size()).isEqualTo(size);
        assertThat(result.content().get(0).name()).startsWith("경리");

        // 커서 페이징 결과 검증
        assertThat(result.hasNext()).isTrue(); // 3개 중 2개만 보여졌으므로 다음 페이지 있음
        assertThat(result.nextCursor()).isNotNull(); // 다음 커서가 있어야 함
        assertThat(result.nextIdAfter()).isNotNull();     // 다음 id 커서도 존재해야 함
    }

    @Test
    void 부서등록() {
        // given
        Department department = new Department("지원", "부서 지원", LocalDate.now(), Instant.now());

        // when
        Department result = departmentRepository.save(department);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("지원");
    }

    @Test
    void 부서_수정() {
        // given
        Department department = new Department("지원", "부서 지원", LocalDate.now(), Instant.now());
        departmentRepository.save(department);

        DepartmentUpdateRequest request = new DepartmentUpdateRequest();
        request.setName("개발 지원");
        request.setDescription("개발을 지원하는 부서");

        // when
        DepartmentDto update = departmentService.update(department.getId(), request);

        // then
        assertThat(update).isNotNull();
        assertThat(update.name()).isEqualTo("개발 지원");
    }

    @Test
    void 부서_삭제_실패() {
        // given
        Department department = new Department("지원", "부서 지원", LocalDate.now(), Instant.now());
        departmentRepository.save(department);

        Employee employee = new Employee(null, department, "강호", "kang@naver.com", "EMP-2025-123312", "사원", LocalDate.now(), EmployeeStatus.ACTIVE, Instant.now());
        employeeRepository.save(employee);

        entityManager.flush();
        entityManager.clear();

        // when & then
        assertThrows(DepartmentDeleteException.class,
                () -> departmentService.delete(department.getId()));
    }



    @Test
    void 부서_삭제_성공() {
        // given
        Department department = new Department("지원", "부서 지원", LocalDate.now(), Instant.now());
        departmentRepository.save(department);

        // 직원 추가 후 제거
        Employee employee = new Employee(null, department, "강호", "kang@naver.com", "EMP-2025-1233213", "사원", LocalDate.now(), EmployeeStatus.ACTIVE, Instant.now());
        employeeRepository.save(employee);

        // 직원 먼저 제거
        employeeRepository.deleteAll();

        entityManager.flush();
        entityManager.clear();

        // when
        departmentService.delete(department.getId());

        // then
        assertFalse(departmentRepository.existsById(department.getId()));
    }

}
