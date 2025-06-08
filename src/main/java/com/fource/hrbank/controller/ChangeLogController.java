package com.fource.hrbank.controller;

import com.fource.hrbank.controller.api.ChangeLogApi;
import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.service.changelog.ChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController implements ChangeLogApi {

    private final ChangeLogService changeLogService;

    @GetMapping
    public ResponseEntity<CursorPageResponseChangeLogDto> findAll(
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String memo,
        @RequestParam(required = false) String ipAddress,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "at") String sortField,
        @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {
        CursorPageResponseChangeLogDto result = changeLogService.findAll(
            employeeNumber, type, memo, ipAddress, idAfter, cursor, size, sortField, sortDirection
        );
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<ChangeDetailDto>> findDiffs(@PathVariable Long id) {
        List<ChangeDetailDto> result = changeLogService.findDiffs(id);
        return ResponseEntity.ok(result);
    }
}
