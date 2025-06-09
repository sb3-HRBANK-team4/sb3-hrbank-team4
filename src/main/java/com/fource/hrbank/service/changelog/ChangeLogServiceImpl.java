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
import com.fource.hrbank.mapper.ChangeLogMapper;
import com.fource.hrbank.repository.ChangeDetailRepository;
import com.fource.hrbank.repository.ChangeLogRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.util.IpUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final EmployeeRepository employeeRepository;
    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogMapper changeLogMapper;
    private final ChangeDetailRepository changeDetailRepository;

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
    public ChangeLogDto create(Employee employee, ChangeType type, String memo, List<ChangeDetailDto> changeDetailDtos) {
        String ipAddress = IpUtils.getCurrentClientIp();

        ChangeLog changeLog = new ChangeLog(
            employee,
            Instant.now(),
            ipAddress,
            type,
            memo,
            null
        );

        ChangeLog savedChangeLog = changeLogRepository.save(changeLog);

        if (changeDetailDtos != null && !changeDetailDtos.isEmpty()) {
            // DTO를 엔티티로 변환하고 changeLog 설정
            List<ChangeDetail> changeDetails = changeDetailDtos.stream()
                .map(dto -> new ChangeDetail(
                    savedChangeLog,  // changeLog 설정
                    dto.getFieldName(),
                    dto.getOldValue(),
                    dto.getNewValue()
                ))
                .toList();
            changeDetailRepository.saveAll(changeDetails);
        }
        return changeLogMapper.toDto(savedChangeLog);
    }

    //변경감지
    public List<ChangeDetailDto> detectChanges(Employee currentEmployee, EmployeeUpdateRequest request, Department newDepartment) {
        List<ChangeDetailDto> details = new ArrayList<>();

        if (!Objects.equals(currentEmployee.getName(), request.name())) {
            details.add(new ChangeDetailDto(null, null, "name", currentEmployee.getName(), request.name()));
        }
        if (!Objects.equals(currentEmployee.getEmail(), request.email())) {
            details.add(new ChangeDetailDto(null, null, "email", currentEmployee.getEmail(), request.email()));
        }
        if (!Objects.equals(currentEmployee.getDepartment().getId(), request.departmentId())) {
            details.add(new ChangeDetailDto(null, null, "department",
                currentEmployee.getDepartment().getName(), newDepartment.getName()));
        }
        if (!Objects.equals(currentEmployee.getPosition(), request.position())) {
            details.add(new ChangeDetailDto(null, null, "position", currentEmployee.getPosition(), request.position()));
        }
        if (!Objects.equals(currentEmployee.getHireDate(), request.hireDate())) {
            details.add(new ChangeDetailDto(null, null, "hireDate",
                currentEmployee.getHireDate().toString(), request.hireDate().toString()));
        }
        if (!Objects.equals(currentEmployee.getStatus(), request.status())) {
            details.add(new ChangeDetailDto(null, null, "status",
                currentEmployee.getStatus().getLabel(), request.status().getLabel()));
        }

        return details;
    }
}