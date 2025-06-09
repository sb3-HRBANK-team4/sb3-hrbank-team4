package com.fource.hrbank.repository;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * jpaSpecificationExecutor<T>
 * where 조건을 코드로 유연하게 조립해 jpa가 처리
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>, EmployeeCustomRepository  {

    public boolean existsByEmail(String email);

    public long countByHireDateBetween(LocalDate start, LocalDate end);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    long countByFilters(EmployeeStatus status, LocalDate from, LocalDate to);
}