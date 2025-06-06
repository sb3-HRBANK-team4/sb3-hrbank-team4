package com.fource.hrbank.controller;

import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.fource.hrbank.dto.department.DepartmentCreateRequest;
import com.fource.hrbank.dto.department.DepartmentDto;
import com.fource.hrbank.service.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 부서 관련 요청을 처리하는 RestController 입니다.
 *
 * 추가, 수정, 삭제, 조회 등의 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public CursorPageResponseDepartmentDto findAll(
            @RequestParam(required = false) String nameOrDescription,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String sortField,
            @RequestParam String sortDirection
    ) {
        CursorPageResponseDepartmentDto departments = departmentService.findAll(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
        return departments;
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> create(@RequestBody DepartmentCreateRequest request) {
        DepartmentDto department = departmentService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(department);
    }
}
