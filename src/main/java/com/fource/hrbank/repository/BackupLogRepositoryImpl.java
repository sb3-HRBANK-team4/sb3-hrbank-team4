package com.fource.hrbank.repository;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.domain.QBackupLog;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BackupLogRepositoryImpl implements BackupLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 백업 로그를 조건과 커서 기반 페이지네이션을 이용하여 조회합니다.
     *
     * @return 조건에 부합하는 백업 로그 리스트
     */
    @Override
    public List<BackupLog> findByCursorCondition(
        String worker, Instant startedAtFrom,
        Instant startedAtTo, BackupStatus status,
        Long idAfter, String cursor,
        String sortField, String sortDirection,
        Pageable pageable
    ) {
        QBackupLog qBackupLog = QBackupLog.backupLog;

        Order direction = "ASC".equalsIgnoreCase(sortDirection) ? Order.ASC : Order.DESC;

        // where 조건 생성을 위한 빌더
        BooleanBuilder where = new BooleanBuilder();

        // 검색 조건 : 작업자_부분일치, 시작시간_범위조건, 상태_완전일치
        if (worker != null) {
            where.and(qBackupLog.worker.contains(worker));
        }
        if (startedAtFrom != null && startedAtTo != null) {
            where.and(qBackupLog.startedAt.between(startedAtFrom, startedAtTo));

        }
        if (status != null) {
            where.and(qBackupLog.status.eq(status));
        }

        // 커서 조건
        if (cursor != null && idAfter != null) {
            Instant cursorInstant = Instant.parse(cursor);
            BooleanBuilder cursorBuilder = createCursorCondition(sortField, direction,
                cursorInstant, idAfter, qBackupLog);
            where.and(cursorBuilder);
        }

        // 정렬 조건
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortField, direction, qBackupLog);

        return queryFactory
            .selectFrom(qBackupLog)
            .where(where)
            .orderBy(orderSpecifier, qBackupLog.id.asc())
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }

    /**
     * 주어진 조건에 따라 전체 백업 로그 개수를 반환합니다.
     *
     * @return 조건에 해당하는 백업 로그 총 개수
     */
    @Override
    public Long countByCondition(String worker, Instant startedAtFrom, Instant startedAtTo,
        BackupStatus status) {
        QBackupLog qBackupLog = QBackupLog.backupLog;

        BooleanBuilder where = new BooleanBuilder();

        if (worker != null) {
            where.and(qBackupLog.worker.contains(worker));
        }
        if (startedAtFrom != null && startedAtTo != null) {
            where.and(qBackupLog.startedAt.between(startedAtFrom, startedAtTo));

        }
        if (status != null) {
            where.and(qBackupLog.status.eq(status));
        }

        Long result = queryFactory
            .select(qBackupLog.count())
            .from(qBackupLog)
            .where(where)
            .fetchOne();

        return result != null ? result : 0L;
    }

    /**
     * 지정된 상태의 가장 최근 백업 정보를 반환합니다.
     *
     * @param status 백업 상태 (기본값: COMPLETED)
     * @return 가장 최근 백업 정보
     */
    @Override
    public Optional<BackupLog> findLatestByStatus(BackupStatus status) {
        QBackupLog qBackupLog = QBackupLog.backupLog;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(qBackupLog)
                        .where(
                                qBackupLog.status.eq(status != null ? status : BackupStatus.COMPLETED)
                        )
                        .orderBy(qBackupLog.endedAt.desc())
                        .fetchFirst());
    }

    @Override
    public Optional<BackupLog> findLatest() {
        QBackupLog qBackupLog = QBackupLog.backupLog;

        return Optional.ofNullable(queryFactory
                .selectFrom(qBackupLog)
                .orderBy(qBackupLog.endedAt.desc())
                .fetchFirst());
    }

    /**
     * 커서 기반 페이징 조건을 생성합니다.
     *
     * @return 커서 조건을 포함한 BooleanBuilder (Where절)
     */
    private BooleanBuilder createCursorCondition(
        String sortField, Order direction,
        Instant cursorInstant, Long idAfter,
        QBackupLog qBackupLog
    ) {
        BooleanBuilder cursorBuilder = new BooleanBuilder();

        if (sortField.equals("startedAt")) {
            if (direction == Order.ASC) {
                cursorBuilder.or(qBackupLog.startedAt.gt(cursorInstant));
                cursorBuilder.or(qBackupLog.startedAt.eq(cursorInstant)
                    .and(qBackupLog.id.gt(idAfter)));
            } else {
                cursorBuilder.or(qBackupLog.startedAt.lt(cursorInstant));
                cursorBuilder.or(qBackupLog.startedAt.eq(cursorInstant)
                    .and(qBackupLog.id.lt(idAfter)));
            }
        } else if (sortField.equals("endedAt")) {
            if (direction == Order.ASC) {
                cursorBuilder.or(qBackupLog.endedAt.gt(cursorInstant));
                cursorBuilder.or(qBackupLog.endedAt.eq(cursorInstant)
                    .and(qBackupLog.id.gt(idAfter)));
            } else {
                cursorBuilder.or(qBackupLog.endedAt.lt(cursorInstant));
                cursorBuilder.or(qBackupLog.endedAt.eq(cursorInstant)
                    .and(qBackupLog.id.lt(idAfter)));
            }
        }

        return cursorBuilder;
    }

    /**
     * 정렬 기준에 따라 OrderSpecifier를 생성합니다.
     *
     * @param sortField  정렬 필드명
     * @param direction  정렬 방향
     * @param qBackupLog QueryDSL용 Q도메인
     * @return 정렬 조건을 나타내는 OrderSpecifier
     */
    private OrderSpecifier createOrderSpecifier(String sortField, Order direction,
        QBackupLog qBackupLog) {

        return switch (sortField) {
            case "startedAt" -> new OrderSpecifier<>(direction, qBackupLog.startedAt);
            case "endedAt" -> new OrderSpecifier<>(direction, qBackupLog.endedAt);
            default -> new OrderSpecifier<>(direction, qBackupLog.startedAt);
        };
    }
}
