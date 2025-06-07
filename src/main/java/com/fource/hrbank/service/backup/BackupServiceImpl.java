package com.fource.hrbank.service.backup;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.fource.hrbank.mapper.BackupLogMapper;
import com.fource.hrbank.repository.BackupLogRepository;
import java.awt.Cursor;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 백업 관련 비지니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final BackupLogRepository backupLogRepository;
    private final BackupLogMapper backupLogMapper;

    /**
     * 검색 조건과 커서 기반 페이지네이션 정보를 이용하여 백업 로그 목록을 조회합니다.
     *
     * @param worker 작업자 이름 (부분일치)
     * @param status 백업 상태 (완전일치)
     * @param startedAtFrom 검색 범위 시작 시간
     * @param startedAtTo 검색 범위 종료 시간
     * @param idAfter 커서 기반 페이징을 위한 기준 ID
     * @param cursor 커서 기준 시간
     * @param size 한 페이지에 조회할 데이터 수
     * @param sortField 정렬 기준 필드명
     * @param sortDirection 정렬 방향
     * @return 커서 기반 페이지 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseBackupDto findAll(String worker, BackupStatus status, Instant startedAtFrom,
            Instant startedAtTo, Long idAfter, String cursor, int size, String sortField,
            String sortDirection) {

        Pageable pageable = PageRequest.of(0, size + 1);

        List<BackupLog> backupLogs = backupLogRepository.findByCursorCondition(worker,startedAtFrom, startedAtTo, status, idAfter, cursor, sortField, sortDirection, pageable);

        boolean hasNext = backupLogs.size() > size;
        String nextCursor = hasNext ? extractCursorValue(backupLogs.get(backupLogs.size() - 1), sortField) : null ;
        Long nextIdAfter = hasNext ? backupLogs.get(backupLogs.size() - 1).getId() : null;

        Long totalElements = backupLogRepository.countByCondition(worker, startedAtFrom, startedAtTo, status);

        return new CursorPageResponseBackupDto(
                backupLogs.stream().map(backupLogMapper::toDto).collect(Collectors.toList()),
                nextCursor,
                nextIdAfter,
                size,
                totalElements,
                hasNext
        );
    }

    /**
     * 정렬 필드에 해당하는 커서 값을 추출합니다.
     *
     * @param backupLog 커서 기준이 될 백업 로그
     * @param sortField 커서 기준 필드명
     * @return 커서 값
     */
    private String extractCursorValue(BackupLog backupLog, String sortField) {
        return switch (sortField) {
            case "startedAt" -> backupLog.getStartedAt().toString();
            case "endedAt" -> backupLog.getEndedAt().toString();
            default -> backupLog.getStartedAt().toString();
        };
    }
}