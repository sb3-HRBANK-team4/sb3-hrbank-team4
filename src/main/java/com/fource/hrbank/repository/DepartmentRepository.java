package com.fource.hrbank.repository;

import com.fource.hrbank.domain.Department;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, DepartmentCustomRepository {

    @Query("SELECT e.department.id, COUNT(e) FROM Employee e WHERE e.department.id IN :departmentIds GROUP BY e.department.id")
    List<Object[]> countByDepartmentIds(@Param("departmentIds") List<Long> departmentIds);
}
