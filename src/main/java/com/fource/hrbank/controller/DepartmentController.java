package com.fource.hrbank.controller;

import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.fource.hrbank.service.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
