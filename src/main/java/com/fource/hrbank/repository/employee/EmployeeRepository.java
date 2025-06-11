package com.fource.hrbank.repository.employee;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * jpaSpecificationExecutor<T> where 조건을 코드로 유연하게 조립해 jpa가 처리
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    JpaSpecificationExecutor<Employee>, EmployeeCustomRepository {
    // 기본 findAll은 자동으로 deleted=false만 조회 (@Where 어노테이션으로)

    boolean existsByEmail(String email);

    long countByStatus(EmployeeStatus status);

    boolean existsByDepartmentId(Long departmentId);

    @Query("""
    SELECT e.department.name, COUNT(e)
    FROM Employee e
    WHERE (:status IS NULL OR e.status = :status)
    GROUP BY e.department.name
""")
    List<Object[]> countByDepartmentGroup(@Param("status") EmployeeStatus status);

    @Query("""
    SELECT e.position, COUNT(e)
    FROM Employee e
    WHERE (:status IS NULL OR e.status = :status)
    GROUP BY e.position
""")
    List<Object[]> countByPositionGroup(@Param("status") EmployeeStatus status);

    @Query("""
    SELECT COUNT(e)
    FROM Employee e
    WHERE (:status IS NULL OR e.status = :status)
""")
    long countAllByStatus(@Param("status") EmployeeStatus status);

    @Query("""
      SELECT COUNT(e)
      FROM Employee e
      WHERE e.status = 'ACTIVE' AND e.hireDate <= :date
    """)
    long countByDateRange(@Param("date") LocalDate date);
}


