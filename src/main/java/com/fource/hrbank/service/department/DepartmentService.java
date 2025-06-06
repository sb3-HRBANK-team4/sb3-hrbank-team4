package com.fource.hrbank.service.department;


import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;

public interface DepartmentService {

    public CursorPageResponseDepartmentDto findAll(String nameOrDescription, Long idAfter, String cursor, int size, String sortField, String sortDirection);
}
