package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.BackupApi;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.fource.hrbank.service.backup.BackupService;
import com.fource.hrbank.service.storage.FileStorage;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 백업 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
public class BackupController implements BackupApi {

    private final BackupService backupService;
    private final FileStorage fileStorage;

    /**
     * 백업 로그 전체를 조회합니다.
     *
     * @return 백업 로그 DTO 리스트 (HTTP 200 OK)
     */
    @GetMapping
    public ResponseEntity<CursorPageResponseBackupDto> findAll(
        @RequestParam(required = false) String worker,
        @RequestParam(required = false) BackupStatus status,
        @RequestParam(required = false) Instant startedAtFrom,
        @RequestParam(required = false) Instant startedAtTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String sortField,
        @RequestParam String sortDirection
    ) {
        CursorPageResponseBackupDto cursorPageResponseBackupDto = backupService.findAll(worker,
            status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(cursorPageResponseBackupDto);
    }

    /**
     * 데이터 백업을 생성합니다.
     *
     * @param request 클라이언트 요청 정보
     * @return 생성된 데이터 백업 이력
     */
    @PostMapping
    public ResponseEntity<BackupDto> backup(HttpServletRequest request) {
        String ipAdress = request.getRemoteAddr();

        BackupDto createBackupDto = backupService.create(ipAdress); // 백업 이력 등록
        BackupDto updateBackupDto = backupService.backup(createBackupDto);  // 데이터 백업 수행

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(updateBackupDto);
    }

    /**
     * 지정된 상태의 가장 최근 백업 정보를 조회합니다. (기본값 :COMPLETED)
     *
     * @param status 백업 상태
     * @return 가장 최근 백업 정보
     */
    @GetMapping("/latest")
    public ResponseEntity<BackupDto> getLatestBackup(@RequestParam(required = false) BackupStatus status) {

        BackupDto backupDto = backupService.findLatestByStatus(status);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(backupDto);
    }
}
