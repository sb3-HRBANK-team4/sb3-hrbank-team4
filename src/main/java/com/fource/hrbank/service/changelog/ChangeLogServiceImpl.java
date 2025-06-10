package com.fource.hrbank.service.changelog;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.exception.ChangeLogNotFoundException;
import com.fource.hrbank.mapper.ChangeDetailMapper;
import com.fource.hrbank.mapper.ChangeLogMapper;
import com.fource.hrbank.repository.change.ChangeDetailRepository;
import com.fource.hrbank.repository.change.ChangeLogRepository;
import com.fource.hrbank.util.IpUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogMapper changeLogMapper;
    private final ChangeDetailRepository changeDetailRepository;
    private final ChangeDetailMapper changeDetailMapper;

    @Override
    public CursorPageResponseChangeLogDto getAllChangeLogs(String employeeNumber, ChangeType type,
        String memo, String ipAddress, Long idAfter, String cursor, int size, String sortField,
        String sortDirection, Instant atFrom, Instant atTo) {

        // 기본값 설정
        if (size <= 0) {
            size = 10;
        }
        if (sortField == null) {
            sortField = "at";
        }
        if (sortDirection == null) {
            sortDirection = "desc";
        }

        // 정렬 필드 검증
        if (!isValidSortField(sortField)) {
            throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다: " + sortField);
        }

        // Repository의 커스텀 메서드 호출
        return changeLogRepository.searchChangeLogsWithSorting(
            employeeNumber, type, memo, ipAddress,
            idAfter, cursor, size, sortField, sortDirection, atFrom, atTo
        );
    }

    private boolean isValidSortField(String sortField) {
        return "ipAddress".equals(sortField) ||
            "at".equals(sortField) || "changedAt".equals(sortField) ||
            "memo".equals(sortField) || "type".equals(sortField);
    }

    public List<DiffsDto> findDiffs(Long changeLogId) {
        ChangeLog changeLog = changeLogRepository.findById(changeLogId)
            .orElseThrow(ChangeLogNotFoundException::new);

        List<ChangeDetail> changeDetails = changeDetailRepository.findByChangeLogId(changeLogId);

        return changeDetails.stream()
            .map(changeDetailMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public ChangeLog create(Employee employee, ChangeType type, String memo,
        List<DiffsDto> diffsDtos) {
        String ipAddress = IpUtils.getCurrentClientIp();

        ChangeLog changeLog = new ChangeLog(
            employee,
            employee.getEmployeeNumber(),
            Instant.now(),
            ipAddress,
            type,
            memo,
            null
        );

        ChangeLog savedChangeLog = changeLogRepository.save(changeLog);

        return savedChangeLog;
    }

    //변경감지
    public List<DiffsDto> detectChanges(Employee currentEmployee,
        EmployeeUpdateRequest request, Department newDepartment) {
        List<DiffsDto> details = new ArrayList<>();

        if (!Objects.equals(currentEmployee.getHireDate(), request.hireDate())) {
            details.add(new DiffsDto("입사일",
                currentEmployee.getHireDate().toString(), request.hireDate().toString()));
        }
        if (!Objects.equals(currentEmployee.getName(), request.name())) {
            details.add(
                new DiffsDto("이름", currentEmployee.getName(), request.name()));
        }
        if (!Objects.equals(currentEmployee.getPosition(), request.position())) {
            details.add(new DiffsDto("직함", currentEmployee.getPosition(),
                request.position()));
        }
        if (!Objects.equals(currentEmployee.getDepartment().getId(), request.departmentId())) {
            details.add(new DiffsDto("부서명",
                currentEmployee.getDepartment().getName(), newDepartment.getName()));
        }
        if (!Objects.equals(currentEmployee.getEmail(), request.email())) {
            details.add(new DiffsDto("이메일", currentEmployee.getEmail(),
                request.email()));
        }
        if (!Objects.equals(currentEmployee.getStatus(), request.status())) {
            details.add(new DiffsDto("상태",
                currentEmployee.getStatus().getLabel(), request.status().getLabel()));
        }

        return details;
    }

    public void saveChangeLogWithDetails(ChangeLog changeLog, List<DiffsDto> dtos) {
        List<ChangeDetail> entities = dtos.stream()
            .map(dto -> {
                ChangeDetail detail = new ChangeDetail();
                detail.setChangeLog(changeLog);
                detail.setPropertyName(dto.getPropertyName());
                detail.setBefore(dto.getBefore());
                detail.setAfter(dto.getAfter());
                return detail;
            })
            .collect(Collectors.toList());

        changeDetailRepository.saveAll(entities);
    }
}