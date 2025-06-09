package com.fource.hrbank.service.department;


import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.fource.hrbank.dto.department.DepartmentCreateRequest;
import com.fource.hrbank.dto.department.DepartmentDto;

public interface DepartmentService {

    public CursorPageResponseDepartmentDto findAll(String nameOrDescription, Long idAfter,
        String cursor, int size, String sortField, String sortDirection);

    public DepartmentDto create(DepartmentCreateRequest request);
}
