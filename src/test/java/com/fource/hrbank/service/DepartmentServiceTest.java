package com.fource.hrbank.service;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.fource.hrbank.repository.DepartmentRepository;
import com.fource.hrbank.service.department.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테이블 삭제
        departmentRepository.deleteAll();

        // 시퀀스를 "테이블의 MAX(id) + 1"로 세팅
        jdbcTemplate.execute("""
            SELECT setval('tbl_department_id_seq', 
                          COALESCE((SELECT MAX(id) FROM tbl_department), 0) + 1,
                          false)
        """);
    }

    @Test
    void findAll_검색조건_없음_커서페이지네이션() {
        // given
        Department department1 = new Department("경리A", "자산 관리 A", Instant.now(), Instant.now());
        Department department2 = new Department("경리B", "자산 관리 B", Instant.now(), Instant.now());
        Department department3 = new Department("경리C", "자산 관리 C", Instant.now(), Instant.now());

        departmentRepository.saveAll(List.of(department1, department2, department3));

        String nameOrDescription = null;
        Long idAfter = null;
        String cursor = null;
        int size = 2;
        String sortField = "name";
        String sortDirection = "asc";

        // when
        CursorPageResponseDepartmentDto result = departmentService.findAll(
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
        Department department = new Department("지원", "부서 지원", Instant.now(), Instant.now());

        // when
        Department result = departmentRepository.save(department);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("지원");
    }
}
