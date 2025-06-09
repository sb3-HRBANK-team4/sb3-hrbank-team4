package com.fource.hrbank.service.changelog;

import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.ChangeLogCreateRequestDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.mapper.ChangeLogMapper;
import com.fource.hrbank.repository.ChangeLogRepository;
import com.fource.hrbank.repository.EmployeeRepository;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final EmployeeRepository employeeRepository;
    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogMapper changeLogMapper;

    @Override
    public CursorPageResponseChangeLogDto findAll(
        String employeeNumber,
        String type,
        String memo,
        String ipAddress,
        Long idAfter,
        String cursor,
        int size,
        String sortField,
        String sortDirection
    ) {
        return new CursorPageResponseChangeLogDto(
            List.of(),
            null,
            null,
            size,
            0L,
            false
        );
    }

    @Override
    public ChangeLogDto findById(Long id) {
        ChangeLog changeLog = changeLogRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 변경 이력을 찾을 수 없습니다. id=" + id));
        return changeLogMapper.toDto(changeLog);
    }

    @Override
    public List<ChangeDetailDto> findDiffs(Long id) {
        return List.of();
    }

    @Override
    public ChangeLogDto create(ChangeLogCreateRequestDto request) {
        Employee employee = employeeRepository.findByEmployeeNumber(request.getEmployeeNumber())
            .orElseThrow(() -> new IllegalArgumentException("해당 사번의 직원을 찾을 수 없습니다. employeeNumber=" + request.getEmployeeNumber()));

        ChangeLog changeLog = new ChangeLog(
            employee,
            Instant.now(),
            request.getIpAddress(),
            request.getType(),
            request.getMemo(),
            null
        );
        return changeLogMapper.toDto(changeLogRepository.save(changeLog));
    }
}