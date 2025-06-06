package com.fource.hrbank.controller.api;

import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
public interface BackupApi {

    @Operation(
        summary = "데이터 백업 목록 조회",
        description = "데이터 백업 목록을 조회합니다.",
        operationId = "getAllBackups"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CursorPageResponseBackupDto.class))
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
    ResponseEntity<CursorPageResponseEmployeeDto> getAllBackups(
        @Parameter(description = "작업자")
        @RequestParam(value = "worker", required = false) String worker,

        @Parameter(description = "상태 (IN_PROGRESS, COMPLETED, FAILED)")
        @RequestParam(value = "status", required = false) String status,

        @Parameter(description = "시작 시간(부터)")
        @RequestParam(value = "startedAtFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAtFrom,

        @Parameter(description = "시작 시간(까지)")
        @RequestParam(value = "startedAtTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAtTo,

        @Parameter(description = "이전 페이지 마지막 요소 ID")
        @RequestParam(value = "idAfter", required = false) Integer idAfter,

        @Parameter(description = "커서 (다음 페이지 시작점)")
        @RequestParam(value = "cursor", required = false) String cursor,

        @Parameter(description = "페이지 크기 (기본값: 10)")
        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,

        @Parameter(description = "정렬 필드 (name, employeeNumber, hireDate)")
        @RequestParam(value = "sortField", required = false, defaultValue = "name") String sortField,

        @Parameter(description = "정렬 방향 (asc 또는 desc, 기본값: asc)")
        @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection
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
        BackupCreateResponse backupCreateResponse
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
        @Parameter(
            description = "백업 상태 (COMPLETED, FAILED, IN_PROGRESS, 기본값: COMPLETED)"
        )
        @RequestParam(value = "status", required = false, defaultValue = "COMPLETED")
        String status
    );
}
