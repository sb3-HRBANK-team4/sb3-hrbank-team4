package com.fource.hrbank.repository.change;

import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.QChangeLog;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChangeLogCustomRepositoryImpl implements ChangeLogCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QChangeLog changeLog = QChangeLog.changeLog;


    @Override
    public CursorPageResponse<ChangeLogDto> searchChangeLogsWithSorting(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Long idAfter,
        String cursor,
        int size,
        String sortField,
        String sortDirection,
        Instant atFrom,
        Instant atTo
    ) {

        // 커서 처리
        if (cursor != null && !cursor.isEmpty()) {
            try {
                idAfter = Long.parseLong(cursor);
            } catch (NumberFormatException e) {
                // 커서 파싱 실패시 무시
            }
        }

        // 동적 조건 생성
        BooleanBuilder whereCondition = createWhereCondition(
            employeeNumber, type, memo, ipAddress, idAfter, atFrom, atTo);

        // 정렬 조건 생성
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(sortField, sortDirection);
        OrderSpecifier<Long> idOrder = changeLog.id.asc(); // 안정적 정렬을 위한 ID 정렬

        // 데이터 조회 (size + 1로 다음 페이지 존재 여부 확인)
        List<ChangeLogDto> content = queryFactory
            .select(Projections.constructor(ChangeLogDto.class,
                changeLog.id,
                changeLog.employeeNumber,
                changeLog.changedAt,
                changeLog.changedIp,
                changeLog.type,
                changeLog.memo))
            .from(changeLog)
            .where(whereCondition)
            .orderBy(orderSpecifier, idOrder)
            .limit(size + 1)
            .fetch();

        // 다음 페이지 존재 여부 확인
        boolean hasNext = content.size() > size;
        if (hasNext) {
            content = content.subList(0, size);
        }

        // 다음 커서 생성
        String nextCursor = null;
        Long nextIdAfter = null;
        if (hasNext && !content.isEmpty()) {
            ChangeLogDto lastItem = content.get(content.size() - 1);
            nextCursor = String.valueOf(lastItem.getId());
            nextIdAfter = lastItem.getId();
        }

        // 전체 카운트 조회
        Long totalElements = queryFactory
            .select(changeLog.count())
            .from(changeLog)
            .where(createWhereConditionForCount(employeeNumber, type, memo, ipAddress))
            .fetchOne();

        return new CursorPageResponse<ChangeLogDto>(
            content,
            nextCursor,
            nextIdAfter,
            size,
            totalElements != null ? totalElements : 0L,
            hasNext
        );
    }

    private BooleanBuilder createWhereCondition(String employeeNumber, ChangeType type,
        String memo, String ipAddress, Long idAfter, Instant atFrom, Instant atTo) {
        BooleanBuilder builder = new BooleanBuilder();

        // 부분 일치 조건
        if (employeeNumber != null && !employeeNumber.isEmpty()) {
            builder.and(changeLog.employeeNumber.containsIgnoreCase(employeeNumber));
        }

        if (memo != null && !memo.isEmpty()) {
            builder.and(changeLog.memo.containsIgnoreCase(memo));
        }

        if (ipAddress != null && !ipAddress.isEmpty()) {
            builder.and(changeLog.changedIp.containsIgnoreCase(ipAddress));
        }

        if (type != null) {
            builder.and(changeLog.type.eq(type));
        }

        // 커서 조건
        if (idAfter != null) {
            builder.and(changeLog.id.gt(idAfter));
        }

        if (atFrom != null) {
            builder.and(changeLog.changedAt.goe(atFrom));
        }
        if (atTo != null) {
            builder.and(changeLog.changedAt.loe(atTo));
        }

        return builder;

    }

    private BooleanBuilder createWhereConditionForCount(String employeeNumber, ChangeType type,
        String memo, String ipAddress) {
        BooleanBuilder builder = new BooleanBuilder();

        if (employeeNumber != null && !employeeNumber.isEmpty()) {
            builder.and(changeLog.employeeNumber.containsIgnoreCase(employeeNumber));
        }

        if (memo != null && !memo.isEmpty()) {
            builder.and(changeLog.memo.containsIgnoreCase(memo));
        }

        if (ipAddress != null && !ipAddress.isEmpty()) {
            builder.and(changeLog.changedIp.containsIgnoreCase(ipAddress));
        }

        if (type != null) {
            builder.and(changeLog.type.eq(type));
        }

        return builder;
    }


    private OrderSpecifier<?> createOrderSpecifier(String sortField, String sortDirection) {
        Order order = sortDirection.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        return switch (sortField) {
            case "ipAddress" -> order == Order.ASC ?
                changeLog.changedIp.asc() : changeLog.changedIp.desc();
            case "memo" -> order == Order.ASC ?
                changeLog.memo.asc() : changeLog.memo.desc();
            case "type" -> order == Order.ASC ?
                changeLog.type.asc() : changeLog.type.desc();
            case "at", "changedAt" -> order == Order.ASC ?
                changeLog.createdAt.asc() : changeLog.createdAt.desc();
            default -> order == Order.ASC ?
                changeLog.createdAt.asc() : changeLog.createdAt.desc();
        };
    }
}