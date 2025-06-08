package com.fource.hrbank.dto.changelog;

import com.fource.hrbank.domain.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogDto {
    private Long id;
    private String employeeNumber;
    private Instant changedAt;
    private String changedIp;
    private ChangeType type;
    private String memo;
}