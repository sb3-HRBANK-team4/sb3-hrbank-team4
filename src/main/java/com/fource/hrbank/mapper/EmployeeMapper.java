package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.employee.EmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "profile.id", target = "profileImageId")
    EmployeeDto toDto(Employee employee);
}
