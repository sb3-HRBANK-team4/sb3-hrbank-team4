package com.fource.hrbank.repository;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * jpaSpecificationExecutor<T>
 * where 조건을 코드로 유연하게 조립해 jpa가 처리
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {


    @Query("""
      SELECT COUNT(e) FROM Employee e
      WHERE (:status IS NULL OR e.status = :status)
        AND (:from IS NULL OR e.hireDate >= :from)
        AND (:to IS NULL OR e.hireDate <= :to)
    """)
    long countByFilters(
        @Param("status") EmployeeStatus status,
        @Param("from") Date from,
        @Param("to") Date to
    );
}

