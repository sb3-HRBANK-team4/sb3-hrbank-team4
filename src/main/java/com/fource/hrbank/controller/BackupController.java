package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.BackupApi;
import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.fource.hrbank.service.backup.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * 백업 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
public class BackupController implements BackupApi {

    private final BackupService backupService;

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
        CursorPageResponseBackupDto cursorPageResponseBackupDto = backupService.findAll(worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(cursorPageResponseBackupDto);
    }
}
