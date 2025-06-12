package com.fource.hrbank.controller.api;

import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
public interface ChangeLogApi {

    @Operation(
        summary = "직원 정보 수정 이력 목록 조회",
        description = "직원 정보 수정 이력 목록을 조회합니다. 상세 변경 내용은 포함되지 않습니다.",
        operationId = "getAllChangeLogs"
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
    ResponseEntity<CursorPageResponse<ChangeLogDto>> getAllChangeLogs(
        @Parameter(description = "대상 직원 사번")
        @RequestParam(value = "employeeNumber", required = false) String employeeNumber,

        @Parameter(description = "이력 유형 (CREATED, UPDATED, DELETED)")
        @RequestParam(value = "type", required = false) ChangeType type,

        @Parameter(description = "내용 (메모)")
        @RequestParam(value = "memo", required = false) String memo,

        @Parameter(description = "IP 주소")
        @RequestParam(value = "ipAddress", required = false) String ipAddress,

        @Parameter(description = "이전 페이지 마지막 요소 ID")
        @RequestParam(value = "idAfter", required = false) Long idAfter,

        @Parameter(description = "커서 (이전 페이지의 마지막 ID)")
        @RequestParam(value = "cursor", required = false) String cursor,

        @Parameter(description = "페이지 크기")
        @RequestParam(value = "size", required = false, defaultValue = "10") int size,

        @Parameter(description = "정렬 필드 (ipAddress, at)")
        @RequestParam(value = "sortField", required = false, defaultValue = "at") String sortField,

        @Parameter(description = "정렬 방향 (asc, desc)")
        @RequestParam(value = "sortDirection", required = false, defaultValue = "desc") String sortDirection,

        @Parameter(description = "수정 일시(부터)")
        @RequestParam(value = "atFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant atFrom,

        @Parameter(description = "수정 일시(까지)")
        @RequestParam(value = "atTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant atTo
    );

    @Operation(
        summary = "직원 정보 수정 이력 상세 조회",
        description = "직원 정보 수정 이력의 상세 정보를 조회합니다. 변경 상세 내용이 포함됩니다.",
        operationId = "getChangeLogDiffs"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "조회 성공"
        ),
        @ApiResponse(
            responseCode = "404", description = "이력을 찾을 수 없음 ",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<List<DiffsDto>> getChangeLogDiffs(
        @Parameter(required = true, description = "이력 ID") Long id
    );

    @Operation(
        summary = "수정 이력 건수 조회",
        description = "직원 정보 수정 이력 건수를 조회합니다. 파라미터를 제공하지 않으면 최근 일주일 데이터를 반환합니다.",
        operationId = "getChangeLogsCount"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "조회 성공"
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 날짜 범위",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Long> getChangeLogsCount(
        @Parameter(description = "시작 일시 (기본값: 7일 전)")
        @RequestParam(value = "fromDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,

        @Parameter(description = "종료 일시 (기본값: 현재)")
        @RequestParam(value = "toDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate
    );

}
