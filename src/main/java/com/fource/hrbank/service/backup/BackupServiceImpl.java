package com.fource.hrbank.service.backup;

import com.fource.hrbank.domain.BackupLog;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.exception.BackupLogNotFoundException;
import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.mapper.BackupLogMapper;
import com.fource.hrbank.mapper.EmployeeMapper;
import com.fource.hrbank.repository.backup.BackupLogRepository;
import com.fource.hrbank.repository.ChangeLogRepository;
import com.fource.hrbank.repository.EmployeeRepository;
import com.fource.hrbank.repository.FileMetadataRepository;
import com.fource.hrbank.service.storage.FileStorage;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 백업 관련 비지니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final BackupLogRepository backupLogRepository;
    private final ChangeLogRepository changeLogRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorage fileStorage;
    private final FileMetadataRepository fileMetadataRepository;

    private final BackupLogMapper backupLogMapper;
    private final EmployeeMapper employeeMapper;

    /**
     * 검색 조건과 커서 기반 페이지네이션 정보를 이용하여 백업 로그 목록을 조회합니다.
     *
     * @param worker        작업자 이름 (부분일치)
     * @param status        백업 상태 (완전일치)
     * @param startedAtFrom 검색 범위 시작 시간
     * @param startedAtTo   검색 범위 종료 시간
     * @param idAfter       커서 기반 페이징을 위한 기준 ID
     * @param cursor        커서 기준 시간
     * @param size          한 페이지에 조회할 데이터 수
     * @param sortField     정렬 기준 필드명
     * @param sortDirection 정렬 방향
     * @return 커서 기반 페이지 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseBackupDto findAll(String worker, BackupStatus status, Instant startedAtFrom,
            Instant startedAtTo, Long idAfter, String cursor, int size, String sortField,
            String sortDirection) {

        Pageable pageable = PageRequest.of(0, size + 1);

        List<BackupLog> backupLogs = backupLogRepository.findByCursorCondition(worker, startedAtFrom, startedAtTo, status, idAfter, cursor, sortField, sortDirection, pageable);

        boolean hasNext = backupLogs.size() > size;
        String nextCursor = hasNext ? extractCursorValue(backupLogs.get(backupLogs.size() - 1), sortField) : null;
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
     * 지정된 상태의 가장 최근 백업 이력을 조회합니다.
     *
     * @param status 백업 상태
     * @return 가장 최근 백업 이력 DTO
     * @throws BackupLogNotFoundException
     */
    @Override
    public BackupDto findLatestByStatus(BackupStatus status) {
        BackupLog backupLog = backupLogRepository.findLatestByStatus(status)
                .orElseThrow(BackupLogNotFoundException::new);

        return backupLogMapper.toDto(backupLog);
    }

    /**
     * 백업 이력을 생성합니다.
     *
     * @param ipAdress 요청자 IP 주소
     * @return 생성된 백업 이력 DTO
     */
    @Override
    public BackupDto create(String ipAdress) {
        // STEP 1. 필요여부 판단_백업이 필요없다면 건너뜀 상태로 배치 이력을 저장하고 프로세스 종료
        // STEP 2. 필요시 데이터 백업 이력 등록 (작업자_요청자 IP주소, 상태_진행중)
        BackupStatus backupStatus = isRequiredBackup() ? BackupStatus.IN_PROGRESS : BackupStatus.SKIPPED;

        BackupLog backupLog = new BackupLog(
                ipAdress, Instant.now(), Instant.now(), backupStatus, null
        );

        backupLogRepository.save(backupLog);
        return backupLogMapper.toDto(backupLog);
    }

    /**
     * 실제 데이터 백업 작업을 수행합니다.
     *
     * @param backupDto 백업 이력 DTO
     * @return 수행된 백업 이력 DTO
     */
    @Override
    public BackupDto backup(BackupDto backupDto) {
        // 건너뜀 상태라면 백업 진행하지 않음
        if (backupDto.status() == BackupStatus.SKIPPED) {
            return backupDto;
        }

        // STEP 3. 실제 데이터 백업 작업 수행
        // 전체 직원 정보를 CSV 파일로 변환
        List<EmployeeDto> employeeDtos = employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());

        String employeeCsv = CsvFormatter.toCsv(employeeDtos);
        FileMetadata metadata = null;

        try {
            // STEP 4. 백업 성공 시 직원 정보 파일 저장 + 이력 업데이트
            fileStorage.put(backupDto.id(), employeeCsv.getBytes(StandardCharsets.UTF_8));

            metadata = new FileMetadata(
                    "employee_backup_" + backupDto.id() + "_" + Instant.now() + ".csv",
                    "text/csv",
                    (long) employeeCsv.getBytes(StandardCharsets.UTF_8).length
            );
            fileMetadataRepository.save(metadata);

            return update(backupDto.id(), BackupStatus.COMPLETED, metadata);
        } catch (Exception e) {
            // STEP 4. 백업 실패 시 에러 로그 파일 저장 + 이력 업데이트
            String errorLog = ExceptionUtils.getStackTrace(e);

            try {
                fileStorage.put(backupDto.id(), errorLog.getBytes(StandardCharsets.UTF_8));

                metadata = new FileMetadata(
                        "backup_error_" + backupDto.id() + "_" + Instant.now() + ".log",
                        "text/plain",
                        (long) errorLog.getBytes(StandardCharsets.UTF_8).length
                );
                fileMetadataRepository.save(metadata);
            } catch (Exception ex) {
                throw new FileIOException(FileIOException.FILE_SAVE_ERROR_MESSAGE);
            }

            return update(backupDto.id(), BackupStatus.FAILED, metadata);
        }
    }

    /**
     *
     * @return
     */
    @Override
    @Scheduled(fixedDelayString = "${hrbank.batch.time}")
    public BackupDto batchBackup() {
        BackupDto result = create("system");
        result = backup(result);

        return result;
    }

    /**
     * 백업 필요 여부를 판단합니다.
     * 가장 최근 완료된 백업 이후 직원 정보가 변경된 경우 true 반환
     *
     * @return 백업 필요 여부
     * @throws BackupLogNotFoundException
     */
    private Boolean isRequiredBackup() {

        return backupLogRepository.findLatest()
                .map(backupLog -> changeLogRepository.existsByChangedAtAfter(backupLog.getEndedAt()))
                .orElse(true); // 백업 이력이 없다면 백업 필요
    }

    /**
     * 백업 이력 상태를 수정합니다.
     *
     * @param id 수정할 백업 이력 ID
     * @param status 새로운 백업 상태값
     * @return 수정된 백업 이력 DTO
     */
    private BackupDto update(Long id, BackupStatus status, FileMetadata metadata) {
        BackupLog backupLog = backupLogRepository.findById(id)
                .orElseThrow(BackupLogNotFoundException::new);

        backupLog.setEndedAt(Instant.now());
        backupLog.setStatus(status);
        backupLog.setBackupFile(metadata);

        return backupLogMapper.toDto(backupLogRepository.save(backupLog));
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