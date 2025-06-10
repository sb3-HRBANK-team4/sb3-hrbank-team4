package com.fource.hrbank.service.changelog;

import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.ChangeDetailDto;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
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
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogMapper changeLogMapper;
    private final ChangeDetailRepository changeDetailRepository;
    private final ChangeDetailMapper changeDetailMapper;

    @Override
    public CursorPageResponseChangeLogDto getAllChangeLogs(String employeeNumber, ChangeType type,
        String memo, String ipAddress, Long idAfter, String cursor, int size, String sortField,
        String sortDirection) {

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
            idAfter, cursor, size, sortField, sortDirection
        );
    }

    private boolean isValidSortField(String sortField) {
        return "ipAddress".equals(sortField) || "ip".equals(sortField) ||
            "at".equals(sortField) || "changedAt".equals(sortField) ||
            "memo".equals(sortField) || "type".equals(sortField);
    }

    public List<ChangeDetailDto> findDiffs(Long changeLogId) {
        ChangeLog changeLog = changeLogRepository.findById(changeLogId)
            .orElseThrow(ChangeLogNotFoundException::new);

        List<ChangeDetail> changeDetails = changeDetailRepository.findByChangeLogId(changeLogId);
//
//        // 변경사항이 없으면 현재 상태 정보 반환
//        if (changeDetails.isEmpty() && changeLog.getType() == ChangeType.DELETED) {
//            return createSimpleSnapshot(changeLog);
//        }

        return changeDetails.stream()
            .map(changeDetailMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public ChangeLogDto create(Employee employee, ChangeType type, String memo,
        List<ChangeDetailDto> changeDetailDtos) {
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
        setChangeLogId(changeDetailDtos, savedChangeLog.getId());

        return changeLogMapper.toDto(savedChangeLog);
    }

    //변경감지
    public List<ChangeDetailDto> detectChanges(Employee currentEmployee,
        EmployeeUpdateRequest request, Department newDepartment) {
        List<ChangeDetailDto> details = new ArrayList<>();

        if (!Objects.equals(currentEmployee.getHireDate(), request.hireDate())) {
            details.add(new ChangeDetailDto("입사일",
                currentEmployee.getHireDate().toString(), request.hireDate().toString()));
        }
        if (!Objects.equals(currentEmployee.getName(), request.name())) {
            details.add(
                new ChangeDetailDto("이름", currentEmployee.getName(), request.name()));
        }
        if (!Objects.equals(currentEmployee.getPosition(), request.position())) {
            details.add(new ChangeDetailDto("직함", currentEmployee.getPosition(),
                request.position()));
        }
        if (!Objects.equals(currentEmployee.getDepartment().getId(), request.departmentId())) {
            details.add(new ChangeDetailDto("부서명",
                currentEmployee.getDepartment().getName(), newDepartment.getName()));
        }
        if (!Objects.equals(currentEmployee.getEmail(), request.email())) {
            details.add(new ChangeDetailDto("이메일", currentEmployee.getEmail(),
                request.email()));
        }
        if (!Objects.equals(currentEmployee.getStatus(), request.status())) {
            details.add(new ChangeDetailDto("상태",
                currentEmployee.getStatus().getLabel(), request.status().getLabel()));
        }

        return details;
    }

    public List<ChangeDetailDto> setChangeLogId(List<ChangeDetailDto> details, Long changeLogId) {
        return details.stream()
            .map(detail -> new ChangeDetailDto(
                detail.getPropertyName(),
                detail.getBefore(),
                detail.getAfter()
            ))
            .collect(Collectors.toList());
    }

//    private List<ChangeDetailDto> createSimpleSnapshot(ChangeLog changeLog) {
//        List<ChangeDetailDto> snapshot = new ArrayList<>();
//        Long changeLogId = changeLog.getId();
//
//        if (changeLog.getEmployee() != null) {
//            Employee employee = changeLog.getEmployee();
//
//            // 현재 값만 "변경 후" 필드에 설정, "변경 전"은 "-"
//            snapshot.add(new ChangeDetailDto("입사일", "-", employee.getHireDate().toString()));
//            snapshot.add(new ChangeDetailDto("이름", "-", employee.getName()));
//            snapshot.add(new ChangeDetailDto("직함", "-", employee.getPosition()));
//            snapshot.add(new ChangeDetailDto("부서명", "-", employee.getDepartment().getName()));
//            snapshot.add(new ChangeDetailDto("이메일", "-", employee.getEmail()));
//            snapshot.add(new ChangeDetailDto("상태", "-", employee.getStatus().getLabel()));
//        }
//
//        return snapshot;
//    }
}