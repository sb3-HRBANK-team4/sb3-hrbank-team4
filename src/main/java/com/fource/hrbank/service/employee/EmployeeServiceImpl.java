package com.fource.hrbank.service.employee;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.ChangeLog;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDistributionDto;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.exception.DepartmentNotFoundException;
import com.fource.hrbank.exception.DuplicateDepartmentException;
import com.fource.hrbank.exception.DuplicateEmailException;
import com.fource.hrbank.exception.EmployeeNotFoundException;
import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.exception.InsufficientEmployeeDataException;
import com.fource.hrbank.exception.InvalidStatFieldException;
import com.fource.hrbank.mapper.EmployeeMapper;
import com.fource.hrbank.repository.FileMetadataRepository;
import com.fource.hrbank.repository.department.DepartmentRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.repository.employee.EmployeeSpecification;
import com.fource.hrbank.service.changelog.ChangeLogService;
import com.fource.hrbank.service.storage.FileStorage;
import com.fource.hrbank.util.IpUtils;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 직원 관련 비즈니스 로직을 담당하는 클래스입니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Logging
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;
    private final FileStorage fileStorage;
    private final FileMetadataRepository fileMetadataRepository;
    private final ChangeLogService changeLogService;

    /**
     * 직원 등록 이메일 중복 검증 프로필 이미지 파일 저장 (선택) 사원번호 자동 생성 (형식: EMP-YYYY-timestamp) 직원 상태를 ACTIVE로 초기화 변경
     * 이력(ChangeLog) 자동 생성
     *
     * @param request      직원 생성 요청 정보 (이름, 이메일, 부서ID, 직함, 입사일, 메모 포함)
     * @param profileImage 프로필 이미지 (선택)
     * @return 생성된 직원 정보 DTO
     */
    @Transactional
    @Override
    public EmployeeDto create(EmployeeCreateRequest request, Optional<MultipartFile> profileImage) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        FileMetadata profile = null;

        // 프로필 이미지 저장 처리
        if (profileImage.isPresent() && !profileImage.get().isEmpty()) {
            MultipartFile file = profileImage.get();
            log.info("프로필 이미지 저장 - name: {}", profileImage.get().getOriginalFilename());

            // 메타정보 생성 및 저장
            FileMetadata metadata = new FileMetadata(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
            FileMetadata savedMetadata = fileMetadataRepository.save(metadata);
            log.info("메타데이터 저장 완료 - ID: {}", savedMetadata.getId());

            // 바이트 저장
            try {
                fileStorage.put(savedMetadata.getId(), file.getBytes());
                profile = savedMetadata;
            } catch (IOException e) {
                log.error("파일 저장 실패: {}", e.getMessage());
                throw new FileIOException(ResponseMessage.FILE_SAVE_ERROR_MESSAGE,
                    ResponseDetails.FILE_SAVE_ERROR_MESSAGE);
            }
        }

        // 부서 조회
        Department department = departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new DuplicateDepartmentException());

        //사원번호 예시 : EMP-2025-158861485055084
        String employeeNumber = String.format(
            "EMP-" + Year.now().getValue() + "-" + System.currentTimeMillis());

        Employee employee = new Employee(
            profile,
            department,
            request.name(),
            request.email(),
            employeeNumber,
            request.position(),
            request.hireDate(),
            EmployeeStatus.ACTIVE,
            null
        );

        String ipAddress = IpUtils.getCurrentClientIp();
        log.info("클라이언트 IP 주소: {}", ipAddress);

        Employee savedEmployee = employeeRepository.save(employee);

        // 정보 수정 이력 상세 조회_ 생성시 수정 전 값으로
        List<DiffsDto> diffs = new ArrayList<>();
        diffs.add(new DiffsDto("이름", null, request.name()));
        diffs.add(new DiffsDto("이메일", null, request.email()));
        diffs.add(new DiffsDto("부서명", null, department.getName()));
        diffs.add(new DiffsDto("직함", null, request.position()));
        diffs.add(new DiffsDto("입사일", null, request.hireDate().toString()));
        diffs.add(new DiffsDto("상태", null, EmployeeStatus.ACTIVE.getLabel()));

        ChangeLog changeLog = changeLogService.create(savedEmployee, ChangeType.CREATED, request.memo(), diffs);
        changeLogService.saveChangeLogWithDetails(changeLog, diffs);

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(EmployeeNotFoundException::new);
        return employeeMapper.toDto(employee);
    }

    /**
     * 직원 목록을 조회합니다.
     *
     * @param nameOrEmail    직원 이름 또는 이메일 (부분 일치)
     * @param departmentName 부서명 (부분 일치)
     * @param position       직함 (부분 일치)
     * @param status         직원 상태 (정확히 일치: ACTIVE, ON_LEAVE, RESIGNED)
     * @param sortField      정렬 기준 필드 (예: "name", "employeeNumber", "hireDate")
     * @param sortDirection  정렬 방향 ("ASC" 또는 "DESC")
     * @param cursor         정렬 필드의 마지막 커서 값 (예: 마지막 직원의 이름, 사번, 입사일 등)
     * @param idAfter        마지막 요소의 ID (동일 정렬 필드일 경우 tie-breaker 역할)
     * @param size           조회할 데이터 개수
     * @return 조건에 부합하는 직원 목록 페이지 응답 DTO
     */
    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseEmployeeDto findAll(String nameOrEmail, String employeeNumber,
        String departmentName,
        String position, EmployeeStatus status, String sortField, String sortDirection,
        String cursor, Long idAfter, int size) {

        // 1. 정렬 방향
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection)
            .orElse(Sort.Direction.ASC);

        // 2. 페이징 정보
        Pageable pageable = PageRequest.of(0, size + 1,
            Sort.by(direction, sortField).and(Sort.by("id")));

        // 3. Specification 조합 (검색 조건 + 커서 조건)
        Specification<Employee> spec = Specification
            .where(EmployeeSpecification.nameOrEmailLike(nameOrEmail))
            .and(EmployeeSpecification.departmentContains(departmentName))
            .and(EmployeeSpecification.positionContains(position))
            .and(EmployeeSpecification.statusEquals(status))
            .and(EmployeeSpecification.buildCursorSpec(sortField, cursor, idAfter));

        // 4. 데이터 조회
        List<Employee> employees = employeeRepository.findAll(spec, pageable).getContent();

        // 5. 페이지 분리
        boolean hasNext = employees.size() > size;

        List<EmployeeDto> content = employees.stream()
            .limit(size)
            .map(employeeMapper::toDto)
            .toList();

        // 6. 커서 계산
        String nextCursor =
            hasNext ? extractCursorValue(sortField, content.get(content.size() - 1)) : null;
        Long nextId = hasNext ? content.get(content.size() - 1).id() : null;

        return new CursorPageResponseEmployeeDto(
            content,
            nextCursor,
            nextId,
            size,
            null,
            hasNext
        );
    }

    // 커서 값 생성을 위해 정렬 필드의 값만 추출해 String으로 넘김
    private String extractCursorValue(String sortField, EmployeeDto dto) {
        return switch (sortField) {
            case "name" -> dto.name();
            case "employeeNumber" -> dto.employeeNumber();
            case "hireDate" -> dto.hireDate().toString();
            default -> null;
        };
    }

    /**
     * 직원 정보 수정
     * <p>
     * 직원 존재 여부 검증 이메일 중복 검증(본인 제외) 부서 존재 여부 검증 프로필 이미지 업데이트 (선택) 변경사항 감지 및 상세 이력 생성 변경
     * 이력(ChangeLog) 자동 생성
     *
     * @param id           수정할 직원의 ID
     * @param request      직원 수정 요청 정보 (이름, 이메일, 부서ID, 직함, 입사일, 상태, 메모)
     * @param profileImage 새로운 프로필 이미지 파일 (비어있으면 기존 이미지 유지)
     * @return 수정된 직원의 상세 정보 담은 DTO
     */
    @Transactional
    @Override
    public EmployeeDto update(Long id, EmployeeUpdateRequest request,
        Optional<MultipartFile> profileImage) {
        //1. 수정할 직원 조회 및 존재 여부 검증
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(EmployeeNotFoundException::new);

        //2. email 중복 체크 (본인 이메일 아닌 경우)
        if (!employee.getEmail().equals(request.email()) && employeeRepository.existsByEmail(
            request.email())) {
            throw new DuplicateEmailException();
        }

        //3. 부서 조회 및 존재 여부 검증
        Department department = departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new DepartmentNotFoundException());

        //4. 프로필 이미지 처리
        FileMetadata profile = employee.getProfile(); // 기존 프로필 유지
        if (profileImage.isPresent() && !profileImage.get().isEmpty()) {
            MultipartFile file = profileImage.get();
            FileMetadata metadata = new FileMetadata(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
            FileMetadata savedMetadata = fileMetadataRepository.save(metadata);

            try {
                fileStorage.put(savedMetadata.getId(), file.getBytes());
                profile = savedMetadata;
            } catch (IOException e) {
                throw new FileIOException(ResponseMessage.FILE_SAVE_ERROR_MESSAGE,
                    ResponseDetails.FILE_SAVE_ERROR_MESSAGE);
            }
        }

        List<DiffsDto> diffs = changeLogService.detectChanges(employee, request, department);
        ChangeLog changeLog = changeLogService.create(employee, ChangeType.UPDATED, request.memo(), diffs);
        changeLogService.saveChangeLogWithDetails(changeLog, diffs);

        //7. 실제 업데이트
        employee.update(
            request.name(),
            request.email(),
            department,
            request.position(),
            request.hireDate(),
            request.status(),
            profile
        );

        return employeeMapper.toDto(employee);
    }

    /**
     * id로 직원 삭제, 삭제 이력과 상세변경 내용도 함께 저장됨
     *
     * @param id
     */
    @Transactional
    @Override
    public void deleteById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(EmployeeNotFoundException::new);

        FileMetadata profile = employee.getProfile();
        if (profile != null) {
            try {
                // DB에서 메타데이터 삭제
                fileMetadataRepository.delete(profile);

            } catch (Exception e) {
                log.warn("프로필 이미지 삭제 실패: {}", e.getMessage());
            }
        } else {
            log.info("삭제할 프로필 이미지가 없습니다.");
        }

        List<DiffsDto> diffs = new ArrayList<>();
        diffs.add(new DiffsDto("이름", employee.getName(), null));
        diffs.add(new DiffsDto("이메일", employee.getEmail(), null));
        diffs.add(new DiffsDto("부서명", employee.getDepartment().getName(), null));
        diffs.add(new DiffsDto("직함", employee.getPosition(), null));
        diffs.add(new DiffsDto("입사일", employee.getHireDate().toString(), null));
        diffs.add(new DiffsDto("상태", employee.getStatus().getLabel(), null));

        ChangeLog changeLog = changeLogService.create(employee, ChangeType.DELETED, "직원 삭제", diffs);
        changeLogService.saveChangeLogWithDetails(changeLog, diffs);

        log.info("직원 삭제 완료 - ID: {}", id);
        employeeRepository.delete(employee);
    }

    /**
     * 지정된 그룹 기준을으로 직원 분포 통계
     *
     * @param groupBy 그룹 기준 필드 (예: 부서명, 직함 등)
     * @param status 직원 상태 필터 (enum값, 예: ACTIVE)
     * @return 그룹별 분포 결과 리스트 (비율 포함)
     */
    @Transactional(readOnly = true)
    @Override
    public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
        EmployeeStatus status) {
        try {
            // 전체 직원 수 조회 (퍼센티지 계산용)
            long totalCount = employeeRepository.countByStatus(status);

            // 직원 수 없을 때
            if (totalCount == 0) {
                throw new InsufficientEmployeeDataException();
            }

            // 그룹별 직원 수 조회
            List<EmployeeDistributionDto> distributions = employeeRepository.getDistributionByGroup(
                groupBy, status);

            if (distributions.isEmpty()) {
                log.warn("분포 데이터 없음 groupBy: {}, status: {}, totalCount: {}",
                    groupBy, status, totalCount);
                throw new InsufficientEmployeeDataException();
            }

            // 퍼센티지 계산
            return distributions.stream()
                .map(dist -> new EmployeeDistributionDto(
                    dist.groupKey(),
                    dist.count(),
                    totalCount > 0 ? (double) dist.count() / totalCount * 100.0 : 0.0
                ))
                .collect(Collectors.toList());

        } catch (InsufficientEmployeeDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("직원 분포 조회 중 오류 발생. groupBy: {}, status: {}", groupBy, status, e);
            throw new InvalidStatFieldException();
        }
    }
}
