package com.fource.hrbank.repository;

import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.QChangeLog;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChangeLogRepositoryImpl implements ChangeLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponseChangeLogDto searchChangeLogsWithSorting(
        String employeeNumber,
        String type,
        String memo,
        String ipAddress,
        Long idAfter,
        String cursor,
        int size,
        String sortField,
        String sortDirection
    ) {
        OrderSpecifier<?> order = createOrderSpecifier(sortField, sortDirection);

        return new CursorPageResponseChangeLogDto(
            List.of(), null, null, size, 0L, false
        );
    }

    private OrderSpecifier<?> createOrderSpecifier(String sortField, String sortDirection) {
        Order order = sortDirection.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        PathBuilder<ChangeLog> path = new PathBuilder<>(ChangeLog.class,
            QChangeLog.changeLog.getMetadata());

        return switch (sortField) {
            case "changedIp", "ip" -> new OrderSpecifier<>(order, path.getString("changedIp"));
            case "memo" -> new OrderSpecifier<>(order, path.getString("memo"));
            case "type" -> new OrderSpecifier<>(order, path.getEnum("type", ChangeType.class));
            case "changedAt", "at" ->
                new OrderSpecifier<>(order, path.getDate("changedAt", LocalDate.class));
            default -> new OrderSpecifier<>(order, path.getDate("changedAt", LocalDate.class));
        };
    }
}