package com.fource.hrbank.dto.changelog;

import java.util.List;

public record CursorPageResponseChangeLogDto(
    List<ChangeLogDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext) {

}