package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class ChangeLogMapper {

    public ChangeLogDto toDto(ChangeLog changeLog) {
        return new ChangeLogDto (
            changeLog.getId(),
            changeLog.getEmployee().getEmployeeNumber(),
            changeLog.getChangedAt(),
            changeLog.getChangedIp(),
            changeLog.getType(),
            changeLog.getMemo()
        );
    }
}