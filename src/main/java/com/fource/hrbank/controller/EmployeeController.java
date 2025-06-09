package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.EmployeeApi;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.service.employee.EmployeeService;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;

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
    public ResponseEntity<CursorPageResponseEmployeeDto> getAllEmployees(
        @RequestParam(required = false) String nameOrEmail,
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) String departmentName,
        @RequestParam(required = false) String position,
        @RequestParam(required = false) LocalDate hireDateFrom,
        @RequestParam(required = false) LocalDate hireDateTo,
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(defaultValue = "name") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        CursorPageResponseEmployeeDto employees = employeeService.findAll(
            nameOrEmail, employeeNumber, departmentName, position, status, sortField, sortDirection,
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

}
