package com.fource.hrbank.controller.api;


import com.fource.hrbank.domain.BackupStatus;
import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "데이터 백업 관리")
public interface BackupApi {

    @Operation(
        summary = "데이터 백업 목록 조회",
        description = "데이터 백업 목록을 조회합니다.",
        operationId = "getAllBackups"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CursorPageResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 정렬 필드",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CursorPageResponse<BackupDto>> getAllBackups(
        @Parameter(description = "작업자")
        @RequestParam(value = "worker", required = false) String worker,

        @Parameter(description = "상태 (IN_PROGRESS, COMPLETED, FAILED)")
        @RequestParam(value = "status", required = false) BackupStatus status,

        @Parameter(description = "시작 시간(부터)")
        @RequestParam(value = "startedAtFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant startedAtFrom,

        @Parameter(description = "시작 시간(까지)")
        @RequestParam(value = "startedAtTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant startedAtTo,

        @Parameter(description = "이전 페이지 마지막 요소 ID")
        @RequestParam(value = "idAfter", required = false) Long idAfter,

        @Parameter(description = "커서 (이전 페이지의 마지막 ID)")
        @RequestParam(value = "cursor", required = false) String cursor,

        @Parameter(description = "페이지 크기")
        @RequestParam(value = "size", required = false, defaultValue = "10") int size,

        @Parameter(description = "정렬 필드 (startedAt, endedAt, status)")
        @RequestParam(value = "sortField", required = false, defaultValue = "startedAt") String sortField,

        @Parameter(description = "정렬 방향 (ASC, DESC)")
        @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection
    );

    @Operation(
        summary = "데이터 백업 생성",
        description = "데이터 백업을 생성합니다.",
        operationId = "createBackup"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "백업 생성 성공",
            content = @Content(schema = @Schema(implementation = BackupDto.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409", description = "이미 진행 중인 백업이 있음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<BackupDto> createBackup(
        HttpServletRequest request
    );

    @Operation(
        summary = "최근 백업 정보 조회",
        description = "지정된 상태의 가장 최근 백업 정보를 조회합니다. 상태를 지정하지 않으면 성공적으로 완료된(COMPLETED) 백업을 반환합니다.",
        operationId = "getLatestBackup"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BackupDto.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 상태값",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<BackupDto> getLatestBackup(
        @Parameter(description = "백업 상태 (COMPLETED, FAILED, IN_PROGRESS, 기본값: COMPLETED)")
        @RequestParam(value = "status", required = false, defaultValue = "COMPLETED")
        BackupStatus status
    );
}
