package com.fource.hrbank.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.repository.EmployeeRepository;
import com.fource.hrbank.service.employee.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeServiceTest {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 시퀀스 초기화
        jdbcTemplate.execute("""
            SELECT setval('tbl_employees_id_seq',
                          COALESCE((SELECT MAX(id) FROM tbl_employees), 0) + 1,
                          false)
        """);
    }

    @Test
    @DisplayName("사전 삽입된 직원 ID로 조회 - 정상 동작")
    void findById_사전데이터_정상조회() {
        EmployeeDto result = employeeService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("직원1"); // 사전 데이터에 맞춰
    }
}
