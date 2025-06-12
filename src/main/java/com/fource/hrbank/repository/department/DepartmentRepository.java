package com.fource.hrbank.repository.department;

import com.fource.hrbank.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>,
    DepartmentCustomRepository {

    boolean existsByName(String name);
}
