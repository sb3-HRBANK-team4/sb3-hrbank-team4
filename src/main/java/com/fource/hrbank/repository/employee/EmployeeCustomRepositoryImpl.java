package com.fource.hrbank.repository.employee;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.domain.QEmployee;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countByFilters(EmployeeStatus status, LocalDate from, LocalDate to) {
        QEmployee employee = QEmployee.employee;

        BooleanBuilder where = new BooleanBuilder();
        if (status != null) where.and(employee.status.eq(status));
        if (from != null) where.and(employee.hireDate.goe(from));
        if (to != null) where.and(employee.hireDate.loe(to));

        Long result = queryFactory
            .select(employee.count())
            .from(employee)
            .where(where)
            .fetchOne();

        return result != null ? result : 0L;
  }
}