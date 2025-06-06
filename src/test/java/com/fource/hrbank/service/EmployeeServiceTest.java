package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.repository.EmployeeRepository;
import com.fource.hrbank.service.employee.EmployeeService;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmployeeServiceTest {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

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
        Employee emp1 = new Employee(null, null, "김가", "a@email.com", "EMP-001", "주임", new Date(), EmployeeStatus.ACTIVE, Instant.now());
        Employee savedEmp1 = employeeRepository.save(emp1);

        EmployeeDto result = employeeService.findById(savedEmp1.getId());

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("김가");
    }

}
