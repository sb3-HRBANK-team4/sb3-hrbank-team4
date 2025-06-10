package com.fource.hrbank.mapper;

import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChangeDetailMapper {

    ChangeDetailDto toDto(ChangeDetail changeDetail);

}
