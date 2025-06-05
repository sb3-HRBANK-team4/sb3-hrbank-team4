package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CursorPageResponseDepartmentMapper {

    public CursorPageResponseDepartmentDto toDto(Department department);
}
