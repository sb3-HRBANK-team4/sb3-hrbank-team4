package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.BackupApi;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.service.backup.BackupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Override
    @GetMapping
    public ResponseEntity<List<BackupDto>> findAll() {

        List<BackupDto> backupDtos = backupService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(backupDtos);
    }
}
