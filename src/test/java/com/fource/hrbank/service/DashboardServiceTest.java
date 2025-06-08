package com.fource.hrbank.service;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeCountResponseDto;
import com.fource.hrbank.service.dashboard.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    void getEmployeeCount_조건없음() {
      // given
      // 실제 DB에 사전 데이터가 있다고 가정하거나 테스트용 TestData.sql 로딩

      // when
      EmployeeCountResponseDto result = dashboardService.getEmployeeCount(null, null, null);

      // then
      assertThat(result.getCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void getEmployeeCount_조건있음() {
      Date from = new Date(0);
      Date to = new Date();
      EmployeeCountResponseDto result = dashboardService.getEmployeeCount(EmployeeStatus.ACTIVE, from, to);

      assertThat(result.getCount()).isGreaterThanOrEqualTo(0);
    }
}