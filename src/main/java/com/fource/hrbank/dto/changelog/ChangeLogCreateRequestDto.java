package com.fource.hrbank.dto.changelog;

import com.fource.hrbank.domain.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogCreateRequestDto {
    private String employeeNumber;
    private String memo;
    private String ipAddress;
    private ChangeType type;
}