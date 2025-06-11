package com.fource.hrbank.dto.common;

import java.util.List;

public record CursorPageResponse<T> (
        List<T> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements,
        boolean hasNext
){

}
