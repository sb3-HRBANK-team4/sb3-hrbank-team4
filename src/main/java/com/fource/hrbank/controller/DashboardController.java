package com.fource.hrbank.controller;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.dashboard.EmployeeCountResponseDto;
import com.fource.hrbank.service.dashboard.DashboardService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/count")
  public ResponseEntity<EmployeeCountResponseDto> getEmployeeCount(
      @RequestParam(required = false) EmployeeStatus status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate
  ) {
    EmployeeCountResponseDto response = dashboardService.getEmployeeCount(status, fromDate, toDate);
    return ResponseEntity.ok(response);
  }
}