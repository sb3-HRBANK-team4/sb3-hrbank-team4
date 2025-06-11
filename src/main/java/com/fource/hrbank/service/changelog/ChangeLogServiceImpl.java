package com.fource.hrbank.service.changelog;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.ChangeLogDto;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.exception.ChangeLogNotFoundException;
import com.fource.hrbank.mapper.ChangeDetailMapper;
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

/**
 * 변경 이력, 변경 항목 관련 비즈니스 로직을 담당하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Logging
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeDetailRepository changeDetailRepository;
    private final ChangeDetailMapper changeDetailMapper;

    /**
     * 변경 로그 목록을 조건에 따라 조회
     * 커서 기반 페이지네이션 및 정렬
     *
     * @param employeeNumber 사번
     * @param type 변경 유형 (생성/수정/삭제)
     * @param memo 메모 (검색용)
     * @param ipAddress IP 주소 필터
     * @param idAfter 커서 ID
     * @param cursor 문자열 커서
     * @param size 페이지 크기
     * @param sortField 정렬 필드
     * @param sortDirection 정렬 방향(asd/desc)
     * @param atFrom 조회 시작일시
     * @param atTo 조회 종료일시
     * @return 페이징된 변경 로그 목록
     */
    @Override
    public CursorPageResponse<ChangeLogDto> getAllChangeLogs(
            String employeeNumber,
            ChangeType type,
            String memo,
            String ipAddress,
            Long idAfter,
            String cursor,
            int size,
            String sortField,
            String sortDirection,
            Instant atFrom,
            Instant atTo
    ) {

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

    /**
     * 정렬 필드 유효성 검사
     *
     * @param sortField 사용자 입력 정렬 필드
     * @return 유효 여부
     */
    private boolean isValidSortField(String sortField) {
        return "ipAddress".equals(sortField) ||
            "at".equals(sortField) || "changedAt".equals(sortField) ||
            "memo".equals(sortField) || "type".equals(sortField);
    }

    /**
     * 특정 변경 로그 ID에 해당하는 상세 변경 내역 목록 조회
     *
     * @param changeLogId 변경 로그 ID
     * @return 변경 항목 DTO 리스트
     */
    public List<DiffsDto> findDiffs(Long changeLogId) {
        ChangeLog changeLog = changeLogRepository.findById(changeLogId)
            .orElseThrow(ChangeLogNotFoundException::new);

        List<ChangeDetail> changeDetails = changeDetailRepository.findByChangeLogId(changeLogId);

        return changeDetails.stream()
            .map(changeDetailMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * 변경 로그를 생성하고 저장
     *
     * @param employee 직원 객체
     * @param type 변경 유형
     * @param memo 변경 메모
     * @param diffsDtos 변경 상세 항목 리스트
     * @return 저장된 변경 로그
     */
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

    /**
     * 직원 업데이트 요청에 따른 변경 사항 감지 메서드
     * 이전 값과 요청된 값이 다르면 'DiffsDto'객체로 반환
     *
     * @param currentEmployee 기존 직원 정보
     * @param request 업데이트 요청 정보
     * @param newDepartment 변경된 부서 정보
     * @return 변경된 항목 리스트
     */
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

    /**
     * 변경 로그에 대한 상세 변경 이력을 저장
     *
     * @param changeLog 변경 로그 엔티티
     * @param dtos 변경 항목 리스트
     */
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

    @Override
    public long countByCreatedAtBetween(Instant from, Instant to) {
        return changeLogRepository.countByCreatedAtBetween(from, to);
    }
}