package com.fource.hrbank.service.changelog;

import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.mapper.ChangeLogMapper;
import com.fource.hrbank.repository.ChangeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

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
}
