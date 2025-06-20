package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.ChangeLogApi;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.service.changelog.ChangeLogService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController implements ChangeLogApi {

    private final ChangeLogService changeLogService;

    @GetMapping
    public ResponseEntity<CursorPageResponse<ChangeLogDto>> getAllChangeLogs(
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) ChangeType type,
        @RequestParam(required = false) String memo,
        @RequestParam(required = false) String ipAddress,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "at") String sortField,
        @RequestParam(required = false, defaultValue = "desc") String sortDirection,
        @RequestParam(required = false) Instant atFrom,
        @RequestParam(required = false) Instant atTo
    ) {
        CursorPageResponse<ChangeLogDto> response = changeLogService.getAllChangeLogs(
            employeeNumber, type, memo, ipAddress, idAfter, cursor, size, sortField, sortDirection,
            atFrom, atTo
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<DiffsDto>> getChangeLogDiffs(@PathVariable Long id) {
        List<DiffsDto> result = changeLogService.findDiffs(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getChangeLogsCount(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant atFrom,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant atTo
    ) {

        // null일 경우 기본값 지정 (최근 7일)
        Instant now = Instant.now();
        if (atTo == null) {
            atTo = now;
        }
        if (atFrom == null) {
            atFrom = atTo.minusSeconds(7 * 24 * 60 * 60); // 7일 전
        }

        long count = changeLogService.countByCreatedAtBetween(atFrom, atTo);
        return ResponseEntity.ok(count);
    }
}