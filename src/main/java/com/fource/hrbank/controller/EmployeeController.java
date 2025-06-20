package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.EmployeeApi;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeTrendDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.service.dashboard.DashboardService;
import com.fource.hrbank.service.employee.EmployeeService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;
    private final DashboardService dashboardService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDto> createEmployee(
        @RequestPart("employee") EmployeeCreateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        EmployeeDto employeeDto = employeeService.create(request, Optional.ofNullable(profile));

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(employeeDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @GetMapping
    public ResponseEntity<CursorPageResponse<EmployeeDto>> getAllEmployees(
        @RequestParam(required = false) String nameOrEmail,
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) String departmentName,
        @RequestParam(required = false) String position,
        @RequestParam(required = false) LocalDate hireDateFrom,
        @RequestParam(required = false) LocalDate hireDateTo,
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        CursorPageResponse<EmployeeDto> employees = employeeService.findAll(
            nameOrEmail, employeeNumber, departmentName, position, status, hireDateFrom, hireDateTo,
            sortField, sortDirection,
            cursor, idAfter, size
        );

        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
        @PathVariable Long id,
        @RequestPart("employee") EmployeeUpdateRequest employeeUpdateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        EmployeeDto employee = employeeService.update(id, employeeUpdateRequest,
            Optional.ofNullable(profile));

        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
        @RequestParam(required = false, defaultValue = "department") String groupBy,
        @RequestParam(required = false, defaultValue = "ACTIVE") EmployeeStatus status
    ) {
        List<EmployeeDistributionDto> distribution = dashboardService.getEmployeeDistribution(
            groupBy, status);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> getTrend(
        @RequestParam(name = "from", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

        @RequestParam(name = "to", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

        @RequestParam(name = "unit", defaultValue = "month") String unit
    ) {
        List<com.fource.hrbank.dto.employee.EmployeeTrendDto> trend = dashboardService.getEmployeeTrend(
            from, to, unit);
        return ResponseEntity.ok(trend);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getEmployeeCount(
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        long count = employeeService.getEmployeeCount(status, fromDate, toDate);
        return ResponseEntity.ok(count);
    }


}