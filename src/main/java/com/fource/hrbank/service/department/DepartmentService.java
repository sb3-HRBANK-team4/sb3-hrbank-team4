package com.fource.hrbank.service.department;


import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.department.DepartmentCreateRequest;
import com.fource.hrbank.dto.department.DepartmentDto;
import com.fource.hrbank.dto.department.DepartmentUpdateRequest;

public interface DepartmentService {

    public CursorPageResponse<DepartmentDto> findAll(String nameOrDescription, Long idAfter,
        String cursor, int size, String sortField, String sortDirection);

    public DepartmentDto create(DepartmentCreateRequest request);

    public DepartmentDto update(Long departmentId, DepartmentUpdateRequest request);

    void delete(Long departmentId);
}
