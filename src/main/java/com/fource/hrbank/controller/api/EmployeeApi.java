package com.fource.hrbank.controller.api;

import com.fource.hrbank.dto.employee.EmployeeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;

@Tag(name = "직원 관리")
public interface EmployeeApi {

    @Operation(
        summary = "직원 상세 조회",
        description = "직원 상세 정보를 조회합니다.",
        operationId = "getEmployeeById"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = EmployeeDto.class))
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
    ResponseEntity<EmployeeDto> getEmployeeById(
        @Parameter(required = true, description = "직원 ID") Long id
    );
}
