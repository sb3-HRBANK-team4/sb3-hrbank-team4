package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.DepartmentApi;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.department.DepartmentCreateRequest;
import com.fource.hrbank.dto.department.DepartmentDto;
import com.fource.hrbank.dto.department.DepartmentUpdateRequest;
import com.fource.hrbank.service.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class DepartmentController implements DepartmentApi {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<CursorPageResponse<DepartmentDto>> getAllEmployees(
        @RequestParam(required = false) String nameOrDescription,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        CursorPageResponse<DepartmentDto> departments = departmentService.findAll(nameOrDescription,
            idAfter, cursor, size, sortField, sortDirection);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(departments);
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentCreateRequest request) {
        DepartmentDto department = departmentService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(department);
    }

    @PatchMapping("/{departmentId}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long departmentId,
        @RequestBody DepartmentUpdateRequest request) {
        DepartmentDto department = departmentService.update(departmentId, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(department);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.delete(departmentId);
        return ResponseEntity.noContent().build();
    }
}
