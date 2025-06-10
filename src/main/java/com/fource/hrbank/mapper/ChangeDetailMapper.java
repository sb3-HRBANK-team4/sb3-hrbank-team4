package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.dto.changelog.DiffsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChangeDetailMapper {

    DiffsDto toDto(ChangeDetail changeDetail);

}
