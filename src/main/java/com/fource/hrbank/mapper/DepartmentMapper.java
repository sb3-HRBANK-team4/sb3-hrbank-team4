package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.dto.department.DepartmentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class DepartmentMapper {

    public DepartmentDto toDto(Department department, Long employeeCount) {
        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getEstablishedDate(),
                employeeCount
        );
    }
}
