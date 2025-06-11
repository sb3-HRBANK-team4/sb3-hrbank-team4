package com.fource.hrbank.service.employee;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {

    EmployeeDto create(EmployeeCreateRequest request, Optional<MultipartFile> profileImage);

    EmployeeDto findById(Long id);

    public CursorPageResponse<EmployeeDto> findAll(
        String nameOrEmail, String employeeNumber, String departmentName, String position,
        EmployeeStatus status, LocalDate hireDateFrom, LocalDate hireDateTo,
        String sortField, String sortDirection, String cursor, Long idAfter, int size);

    EmployeeDto update(Long employeeId, EmployeeUpdateRequest request,
        Optional<MultipartFile> profileImage);

//    long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

    void deleteById(Long id);
}
