package com.fource.hrbank.service.employee;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.mapper.EmployeeMapper;
import com.fource.hrbank.repository.EmployeeRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("직원 정보를 찾을 수 없습니다."));
        return employeeMapper.toDto(employee);
    }
}
