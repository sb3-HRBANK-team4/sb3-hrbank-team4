package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.EmployeeApi;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.mapper.EmployeeMapper;
import com.fource.hrbank.service.employee.EmployeeService;
import java.util.Date;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDto> create(
        @RequestPart("EmployeeCreateRequest") EmployeeCreateRequest request,
        @RequestPart(value = "profile", required = false) Long profileImageId
    ) {
        EmployeeDto employeeDto = employeeService.create(request, Optional.ofNullable(profileImageId));

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(employeeDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseEmployeeDto> getEmployees(
        @RequestParam(required = false) String nameOrEmail,
        @RequestParam(required = false) String departmentName,
        @RequestParam(required = false) String position,
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(defaultValue = "name") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(defaultValue = "10") int size
    ) {
        CursorPageResponseEmployeeDto employees = employeeService.findAll(
            nameOrEmail, departmentName, position, status, sortField, sortDirection, cursor, idAfter, size
        );

        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }

}
