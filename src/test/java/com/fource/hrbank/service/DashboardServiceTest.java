package com.fource.hrbank.service;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.EmployeeTrendDto;
import com.fource.hrbank.service.dashboard.DashboardService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.profiles.active}")
    private String profile;

    @BeforeEach
    void setUp() {
        // 테이블 ID 시퀀스 초기화
        if (!profile.equals("local")) {
            jdbcTemplate.execute(
                "TRUNCATE TABLE tbl_change_detail, tbl_change_log, tbl_employees, tbl_department RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void getEmployeeCount_조건없음() {
        // given
        LocalDate from = LocalDate.of(1900, 1, 1);
        LocalDate to = LocalDate.of(2100, 12, 31);

        // when
        EmployeeTrendDto result = dashboardService.getEmployeeCount(null, from, to);

        // then
        assertThat(result.count()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void getEmployeeCount_조건있음() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024,12,31);
        EmployeeTrendDto result = dashboardService.getEmployeeCount(EmployeeStatus.ACTIVE, from, to);

        assertThat(result.count()).isGreaterThanOrEqualTo(0);
    }
}