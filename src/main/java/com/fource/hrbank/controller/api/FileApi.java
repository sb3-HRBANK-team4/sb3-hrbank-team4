package com.fource.hrbank.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;

@Tag(name = "파일 관리", description = "파일 관리 API")
public interface FileApi {

    @Operation(
        summary = "파일 다운로드",
        description = "파일을 다운로드합니다.",
        operationId = "downloadFile"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "다운로드 성공",
            content = @Content(schema = @Schema(type = "string", format = "binary"))
        ),
        @ApiResponse(
            responseCode = "404", description = "파일을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<?> downloadFile(
        @Parameter(required = true, description = "파일 ID") int id
    );

}
