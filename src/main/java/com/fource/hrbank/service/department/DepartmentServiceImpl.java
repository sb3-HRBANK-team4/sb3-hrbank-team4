package com.fource.hrbank.service.department;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.fource.hrbank.mapper.DepartmentMapper;
import com.fource.hrbank.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 부서 관련 비즈니스 로직을 당담하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public CursorPageResponseDepartmentDto findAll(String nameOrDescription, Long idAfter, String cursor, int size, String sortField, String sortDirection) {

        List<Department> departments = departmentRepository.findByCursorCondition(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);

        // 추후 employeeRepository의 직원 집계 메소드 구현 시 추가 작업 예정
        return null;
    }
}
