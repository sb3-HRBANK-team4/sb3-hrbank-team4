package com.fource.hrbank.repository.employee;

import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.domain.QEmployee;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QEmployee employee = QEmployee.employee;

    @PersistenceContext
    private final EntityManager em;

    @Override
    public long countByFilters(EmployeeStatus status, LocalDate from, LocalDate to) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(e) FROM Employee e WHERE 1=1");

        if (status != null) {
            jpql.append(" AND e.status = :status");
        }
        if (from != null) {
            jpql.append(" AND e.hireDate >= :from");
        }
        if (to != null) {
            jpql.append(" AND e.hireDate <= :to");
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

        if (status != null) query.setParameter("status", status);
        if (from != null) query.setParameter("from", from);
        if (to != null) query.setParameter("to", to);

        return query.getSingleResult();
    }

    // 상태별 전체 직원 수 조회
    public long countByStatus(EmployeeStatus status) {
        BooleanBuilder where = new BooleanBuilder();

        if (status != null) {
            where.and(employee.status.eq(status));
        }

        Long result = queryFactory
            .select(employee.count())
            .from(employee)
            .where(where)
            .fetchOne();

        return result != null ? result : 0L;
    }

    // 그룹별 분포 조회
    public List<EmployeeDistributionDto> getDistributionByGroup(String groupBy, EmployeeStatus status) {
        BooleanBuilder where = new BooleanBuilder();

        if (status != null) {
            where.and(employee.status.eq(status));
        }

        // 그룹화 기준에 따라 다른 쿼리 실행
        if ("department".equals(groupBy)) {
            return getDistributionByDepartment(where);
        } else if ("position".equals(groupBy)) {
            return getDistributionByPosition(where);
        } else {
            throw new IllegalArgumentException("지원하지 않는 그룹화 기준입니다: " + groupBy);
        }
    }

    // 부서별 분포 조회
    private List<EmployeeDistributionDto> getDistributionByDepartment(BooleanBuilder where) {
        List<Tuple> results = queryFactory
            .select(employee.department, employee.count())
            .from(employee)
            .where(where)
            .groupBy(employee.department)
            .orderBy(employee.count().desc())
            .fetch();

        return results.stream()
            .map(tuple -> new EmployeeDistributionDto(
                tuple.get(employee.department.name),
                tuple.get(employee.count()),
                0.0 // 퍼센티지는 서비스에서 계산
            ))
            .collect(Collectors.toList());
    }

    // 직무별 분포 조회
    private List<EmployeeDistributionDto> getDistributionByPosition(BooleanBuilder where) {
        List<Tuple> results = queryFactory
            .select(employee.position, employee.count())
            .from(employee)
            .where(where)
            .groupBy(employee.position)
            .orderBy(employee.count().desc())
            .fetch();

        return results.stream()
            .map(tuple -> new EmployeeDistributionDto(
                tuple.get(employee.position),
                tuple.get(employee.count()),
                0.0 // 퍼센티지는 서비스에서 계산
            ))
            .collect(Collectors.toList());
    }
}