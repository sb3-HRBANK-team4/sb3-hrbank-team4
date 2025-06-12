package com.fource.hrbank.controller.api;

import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.department.DepartmentCreateRequest;
import com.fource.hrbank.dto.department.DepartmentDto;
import com.fource.hrbank.dto.department.DepartmentUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@Tag(name = "부서 관리", description = "부서 관리 API")
public interface DepartmentApi {

    @Operation(
        summary = "부서 목록 조회",
        description = "부서 목록을 조회합니다.",
        operationId = "getAllDepartments"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CursorPageResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CursorPageResponse<DepartmentDto>> getAllEmployees(
        @Parameter(description = "부서 이름 또는 설명")
        @RequestParam(value = "nameOrDescription", required = false) String nameOrDescription,

        @Parameter(description = "이전 페이지 마지막 요소 ID")
        @RequestParam(value = "idAfter", required = false) Long idAfter,

        @Parameter(description = "커서 (다음 페이지 시작점)")
        @RequestParam(value = "cursor", required = false) String cursor,

        @Parameter(description = "페이지 크기 (기본값: 10)")
        @RequestParam(value = "size", required = false, defaultValue = "10") int size,

        @Parameter(description = "정렬 필드 (name 또는 establishedDate)")
        @RequestParam(value = "sortField", required = false, defaultValue = "name") String sortField,

        @Parameter(description = "정렬 방향 (asc 또는 desc, 기본값: asc)")
        @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection
    );

    @Operation(
        summary = "부서 등록",
        description = "새로운 부서를 등록합니다.",
        operationId = "createDepartment"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "등록 성공",
            content = @Content(schema = @Schema(implementation = DepartmentDto.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 중복된 이메일",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<DepartmentDto> createDepartment(
        @RequestBody DepartmentCreateRequest departmentCreateRequest
    );

    @Operation(
        summary = "부서 삭제",
        description = "부서를 삭제합니다.",
        operationId = "deleteDepartment"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", description = "삭제 성공"
        ),
        @ApiResponse(
            responseCode = "400", description = "소속 직원이 있는 부서는 삭제할 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", description = "직원을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> deleteDepartment(
        @Parameter(required = true, description = "부서 ID") Long id
    );

    @Operation(
        summary = "부서 수정",
        description = "부서 정보를 수정합니다.",
        operationId = "updateDepartment"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = DepartmentDto.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 중복된 이름",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", description = "부서를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<DepartmentDto> updateDepartment(
        @Parameter(required = true, description = "부서 ID")
        @PathVariable("id") Long id,

        @Parameter(required = true)
        @RequestPart DepartmentUpdateRequest departmentUpdateRequest
    );
}



