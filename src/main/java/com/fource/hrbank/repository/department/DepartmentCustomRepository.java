package com.fource.hrbank.repository.department;

import com.fource.hrbank.domain.Department;
import java.util.List;
import java.util.Map;

public interface DepartmentCustomRepository {

    List<Department> findByCursorCondition(
        String keyword,
        Long lastId,
        String cursorValue,
        int size,
        String sortField,
        String sortDirection
    );

    long countByKeyword(String keyword);

    long countEmployeeByDepartmentId(Long departmentId);

    Map<Long, Long> countByDepartmentIds(List<Long> departmentIds);
}
