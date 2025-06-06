package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.EmployeeApi;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.service.employee.EmployeeService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;

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
