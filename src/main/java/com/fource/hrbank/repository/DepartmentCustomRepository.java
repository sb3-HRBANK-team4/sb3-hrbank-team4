package com.fource.hrbank.repository;

import com.fource.hrbank.domain.Department;
import java.util.List;

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
}
