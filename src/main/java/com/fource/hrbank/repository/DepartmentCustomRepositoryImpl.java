package com.fource.hrbank.repository;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.QDepartment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepartmentCustomRepositoryImpl implements DepartmentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Department> findByCursorCondition(String keyword, Long lastId, String cursorValue,
                                                  int size, String sortField, String sortDirection) {
        QDepartment d = QDepartment.department;

        BooleanBuilder builder = new BooleanBuilder();

        // 키워드 조건
        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.name.containsIgnoreCase(keyword)
                    .or(d.description.containsIgnoreCase(keyword)));
        }

        // 커서 조건
        if (cursorValue != null && lastId != null) {
            if ("name".equals(sortField)) {
                builder.and(d.name.gt(cursorValue)
                        .or(d.name.eq(cursorValue).and(d.id.gt(lastId))));
                if ("desc".equalsIgnoreCase(sortDirection)) {
                    builder = new BooleanBuilder()
                            .and(d.name.lt(cursorValue)
                                    .or(d.name.eq(cursorValue).and(d.id.lt(lastId))));
                }
            } else if ("establishedDate".equals(sortField)) {
                LocalDate parsed = LocalDate.parse(cursorValue);
                builder.and(d.establishedDate.gt(parsed)
                        .or(d.establishedDate.eq(parsed).and(d.id.gt(lastId))));
                if ("desc".equalsIgnoreCase(sortDirection)) {
                    builder = new BooleanBuilder()
                            .and(d.establishedDate.lt(parsed)
                                    .or(d.establishedDate.eq(parsed).and(d.id.lt(lastId))));
                }
            }
        }

        // 정렬
        Order direction = "desc".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
        OrderSpecifier<?> orderSpecifier1;
        if ("name".equals(sortField)) {
            orderSpecifier1 = new OrderSpecifier<>(direction, d.name);
        } else if ("establishedDate".equals(sortField)) {
            orderSpecifier1 = new OrderSpecifier<>(direction, d.establishedDate);
        } else {
            orderSpecifier1 = new OrderSpecifier<>(direction, d.id);
        }

        return queryFactory.selectFrom(d)
                .where(builder)
                .orderBy(orderSpecifier1, new OrderSpecifier<>(direction, d.id))
                .limit(size + 1)
                .fetch();
    }

    @Override
    public long countByKeyword(String keyword) {
        QDepartment d = QDepartment.department;
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.name.containsIgnoreCase(keyword)
                    .or(d.description.containsIgnoreCase(keyword)));
        }

        return queryFactory.select(d.count())
                .from(d)
                .where(builder)
                .fetchOne();
    }
}
