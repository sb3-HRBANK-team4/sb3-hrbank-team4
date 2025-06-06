package com.fource.hrbank.service.backup;

import com.fource.hrbank.dto.backup.BackupDto;
import com.fource.hrbank.mapper.BackupLogMapper;
import com.fource.hrbank.repository.BackupLogRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 백업 관련 비지니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final BackupLogRepository backupLogRepository;
    private final BackupLogMapper backupLogMapper;

    /**
     * 모든 백업 로그를 조회합니다.
     *
     * @return 백업 로그 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<BackupDto> findAll() {

        return backupLogRepository.findAll()
                .stream()
                .map(backupLogMapper::toDto)
                .collect(Collectors.toList());
    }
}
