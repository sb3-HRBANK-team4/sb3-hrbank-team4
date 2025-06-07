package com.fource.hrbank.dto.changelog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDetailDto {
    private Long id;
    private Long changeLogId;
    private String fieldName;
    private String oldValue;
    private String newValue;
}