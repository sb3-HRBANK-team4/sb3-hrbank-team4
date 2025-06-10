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

    private String propertyName;
    private String before;
    private String after;
}