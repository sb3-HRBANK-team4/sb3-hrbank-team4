package com.fource.hrbank.dto.employee;

import java.util.List;

public record CursorPageResponseEmployeeDto(
    List<EmployeeDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}
