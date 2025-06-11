package com.fource.hrbank.service.changelog;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.exception.ChangeLogNotFoundException;
import com.fource.hrbank.mapper.ChangeDetailMapper;
import com.fource.hrbank.repository.change.ChangeDetailRepository;
import com.fource.hrbank.repository.change.ChangeLogRepository;
import com.fource.hrbank.util.IpUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 변경 이력, 변경 항목 관련 비즈니스 로직을 담당하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Logging
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeDetailRepository changeDetailRepository;
    private final ChangeDetailMapper changeDetailMapper;

    // 정렬 필드 상수
    private static final Set<String> VALID_SORT_FIELDS = Set.of(
        "ipAddress", "at", "changedAt", "memo", "type"
    );

    // 기본값 상수
    private static final int DEFAULT_SIZE = 10;
    private static final String DEFAULT_SORT_FIELD = "at";
    private static final String DEFAULT_SORT_DIRECTION = "desc";

    /**
     * 변경 로그 목록을 조건에 따라 조회 커서 기반 페이지네이션 및 정렬
     *
     * @param employeeNumber 사번
     * @param type           변경 유형 (생성/수정/삭제)
     * @param memo           메모 (검색용)
     * @param ipAddress      IP 주소 필터
     * @param idAfter        커서 ID
     * @param cursor         문자열 커서
     * @param size           페이지 크기
     * @param sortField      정렬 필드
     * @param sortDirection  정렬 방향(asd/desc)
     * @param atFrom         조회 시작일시
     * @param atTo           조회 종료일시
     * @return 페이징된 변경 로그 목록
     */
    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseChangeLogDto getAllChangeLogs(String employeeNumber, ChangeType type,
        String memo, String ipAddress, Long idAfter, String cursor, int size, String sortField,
        String sortDirection, Instant atFrom, Instant atTo) {

        // 기본값 적용 및 유효성 검증
        size = size <= 0 ? DEFAULT_SIZE : size;
        sortField = sortField != null ? sortField : DEFAULT_SORT_FIELD;
        sortDirection = sortDirection != null ? sortDirection : DEFAULT_SORT_DIRECTION;

        if (!VALID_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다: " + sortField);
        }

        return changeLogRepository.searchChangeLogsWithSorting(
            employeeNumber, type, memo, ipAddress,
            idAfter, cursor, size, sortField, sortDirection, atFrom, atTo
        );
    }


    /**
     * 특정 변경 로그 ID에 해당하는 상세 변경 내역 목록 조회
     *
     * @param changeLogId 변경 로그 ID
     * @return 변경 항목 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<DiffsDto> findDiffs(Long changeLogId) {
        if (!changeLogRepository.existsById(changeLogId)) {
            throw new ChangeLogNotFoundException();
        }

        List<ChangeDetail> changeDetails = changeDetailRepository.findByChangeLogId(changeLogId);

        return changeDetails.stream()
            .map(changeDetailMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * 변경 로그를 생성하고 저장
     *
     * @param employee  직원 객체
     * @param type      변경 유형
     * @param memo      변경 메모
     * @param diffsDtos 변경 상세 항목 리스트
     * @return 저장된 변경 로그
     */
    @Override
    public ChangeLog create(Employee employee, ChangeType type, String memo, List<DiffsDto> diffsDtos) {
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

        changeLogRepository.save(changeLog);

        ChangeLog savedChangeLog = changeLogRepository.save(changeLog);

        // 변경사항이 있을 때만 상세 내역 저장
        if (diffsDtos != null && !diffsDtos.isEmpty()) {
            List<ChangeDetail> changeDetails = diffsDtos.stream()
                .map(dto -> createChangeDetail(savedChangeLog, dto))
                .collect(Collectors.toList());

            changeDetailRepository.saveAll(changeDetails);

            log.info("변경 로그 저장 완료 - Employee: {}, Type: {}, Changes: {}",
                employee.getEmployeeNumber(), type, diffsDtos.size());
        } else {
            log.debug("변경사항이 없어 상세 내역은 저장하지 않습니다. Employee ID: {}", employee.getId());
        }

        return savedChangeLog;
    }

    /**
     * 두 직원 객체 간의 차이점을 감지하여 DiffsDto 리스트 생성
     *
     * @param before 이전 직원 정보 (null 가능 - 생성 시)
     * @param after  이후 직원 정보 (null 가능 - 삭제 시)
     * @return 변경된 항목들의 리스트
     */
    @Override
    public List<DiffsDto> createEmployeeDiffs(Employee before, Employee after) {
        List<DiffsDto> diffs = new ArrayList<>();

        addDiffIfChanged(diffs, "이름",
            getEmployeeField(before, Employee::getName),
            getEmployeeField(after, Employee::getName));

        addDiffIfChanged(diffs, "이메일",
            getEmployeeField(before, Employee::getEmail),
            getEmployeeField(after, Employee::getEmail));

        addDiffIfChanged(diffs, "부서명",
            getEmployeeField(before, emp -> emp.getDepartment() != null ? emp.getDepartment().getName() : null),
            getEmployeeField(after, emp -> emp.getDepartment() != null ? emp.getDepartment().getName() : null));

        addDiffIfChanged(diffs, "직함",
            getEmployeeField(before, Employee::getPosition),
            getEmployeeField(after, Employee::getPosition));

        addDiffIfChanged(diffs, "입사일",
            getEmployeeField(before, emp -> emp.getHireDate() != null ? emp.getHireDate().toString() : null),
            getEmployeeField(after, emp -> emp.getHireDate() != null ? emp.getHireDate().toString() : null));

        addDiffIfChanged(diffs, "상태",
            getEmployeeField(before, emp -> emp.getStatus() != null ? emp.getStatus().getLabel() : null),
            getEmployeeField(after, emp -> emp.getStatus() != null ? emp.getStatus().getLabel() : null));

        return diffs;
    }

    // ============== Private 헬퍼 메소드들 ==============
    /**
     * Employee 객체에서 안전하게 필드 값을 추출
     * @param employee Employee 객체 (null 가능)
     * @param fieldExtractor 필드 추출 함수
     * @return 추출된 값 (null 가능)
     */
    private String getEmployeeField(Employee employee, Function<Employee, String> fieldExtractor) {
        if (employee == null) {
            return null;
        }
        try {
            return fieldExtractor.apply(employee);
        } catch (Exception e) {
            log.warn("Employee 필드 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    // 값이 다른 경우에만 DiffsDto를 리스트에 추가
    private void addDiffIfChanged(List<DiffsDto> diffs, String propertyName, String before,
        String after) {
        if (!Objects.equals(before, after)) {
            diffs.add(new DiffsDto(propertyName, before, after));
        }
    }

    // ChangeDetail 엔티티 생성
    private ChangeDetail createChangeDetail(ChangeLog changeLog, DiffsDto dto) {
        ChangeDetail detail = new ChangeDetail();
        detail.setChangeLog(changeLog);
        detail.setPropertyName(dto.getPropertyName());
        detail.setBefore(dto.getBefore());
        detail.setAfter(dto.getAfter());
        return detail;
    }
}