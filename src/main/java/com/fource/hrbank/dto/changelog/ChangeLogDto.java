package com.fource.hrbank.dto.changelog;

import com.fource.hrbank.domain.ChangeType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogDto {

    private Long id;
    private String employeeNumber;
    private Instant at;
    private String ipAddress;
    private ChangeType type;
    private String memo;
}