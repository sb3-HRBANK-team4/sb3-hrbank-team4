package com.fource.hrbank.controller;

import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
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

    @GetMapping
    public CursorPageResponseDepartmentDto findAll(
            @RequestParam(required = false) String nameOrDescription,
            @RequestParam Long idAfter,
            @RequestParam String cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String sortField,
            @RequestParam String sortDirection
    ) {

    }
}
