package com.fource.hrbank.controller.api;

import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.dto.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "직원 이력 관리", description = "직원 정보 수정 이력 관련 API입니다.")
@RequestMapping("/api/change-logs")
public interface ChangeLogApi {

    /**
     *  목록 조회: 필터 + 커서 기반 페이징
     */
    @Operation(
        summary = "정보 수정 이력 조회",
        description = "직원 수정 이력을 조건에 따라 필터링하여 조회합니다. 상세 변경 내용은 포함되지 않으며, 커서 기반 페이징을 지원합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CursorPageResponseChangeLogDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (예: 필수 조건 누락, 허용되지 않은 정렬 필드 등)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    ResponseEntity<CursorPageResponseChangeLogDto> findAll(
        @Parameter(description = "직원 사번") @RequestParam(required = false) String employeeNumber,
        @Parameter(description = "변경 유형 (예: ADD, MODIFY, DELETE)") @RequestParam(required = false) String type,
        @Parameter(description = "메모 (부분 일치)") @RequestParam(required = false) String memo,
        @Parameter(description = "IP 주소 (부분 일치)") @RequestParam(required = false) String ipAddress,
        @Parameter(description = "이 커서 ID 이후의 데이터부터 조회") @RequestParam(required = false) Long idAfter,
        @Parameter(description = "기타 커서 문자열") @RequestParam(required = false) String cursor,
        @Parameter(description = "페이지 크기") @RequestParam(required = false, defaultValue = "10") int size,
        @Parameter(description = "정렬 필드 (예: at, ip)") @RequestParam(required = false, defaultValue = "at") String sortField,
        @Parameter(description = "정렬 방향 (asc 또는 desc)") @RequestParam(required = false, defaultValue = "desc") String sortDirection
    );

    /**
     *  상세 diff 조회: 특정 이력의 변경 필드들
     */
    @Operation(
        summary = "정보 수정 이력 상세 조회",
        description = "직원 정보 수정 이력의 상세 변경 내용을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ChangeDetailDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 이력 ID의 상세 내용이 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/diffs")
    ResponseEntity<List<ChangeDetailDto>> findDiffs(
        @Parameter(description = "이력 ID") @PathVariable Long id
    );
}