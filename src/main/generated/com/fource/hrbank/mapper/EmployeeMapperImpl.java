package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.employee.EmployeeDto;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-09T05:26:51+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.jar, environment: Java 17.0.14 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public EmployeeDto toDto(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        Long departmentId = null;
        String departmentName = null;
        Long profileImageId = null;
        Long id = null;
        String name = null;
        String email = null;
        String employeeNumber = null;
        String position = null;
        LocalDate hireDate = null;
        EmployeeStatus status = null;

        departmentId = employeeDepartmentId( employee );
        departmentName = employeeDepartmentName( employee );
        profileImageId = employeeProfileId( employee );
        id = employee.getId();
        name = employee.getName();
        email = employee.getEmail();
        employeeNumber = employee.getEmployeeNumber();
        position = employee.getPosition();
        hireDate = employee.getHireDate();
        status = employee.getStatus();

        EmployeeDto employeeDto = new EmployeeDto( id, name, email, employeeNumber, departmentId, departmentName, position, hireDate, status, profileImageId );

        return employeeDto;
    }

    private Long employeeDepartmentId(Employee employee) {
        Department department = employee.getDepartment();
        if ( department == null ) {
            return null;
        }
        return department.getId();
    }

    private String employeeDepartmentName(Employee employee) {
        Department department = employee.getDepartment();
        if ( department == null ) {
            return null;
        }
        return department.getName();
    }

    private Long employeeProfileId(Employee employee) {
        FileMetadata profile = employee.getProfile();
        if ( profile == null ) {
            return null;
        }
        return profile.getId();
    }
}
