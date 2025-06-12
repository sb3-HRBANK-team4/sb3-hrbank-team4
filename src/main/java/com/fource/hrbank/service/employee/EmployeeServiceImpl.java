package com.fource.hrbank.service.employee;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.ChangeType;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.changelog.DiffsDto;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.exception.DepartmentNotFoundException;
import com.fource.hrbank.exception.DuplicateEmailException;
import com.fource.hrbank.exception.EmployeeNotFoundException;
import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.mapper.EmployeeMapper;
import com.fource.hrbank.repository.FileMetadataRepository;
import com.fource.hrbank.repository.department.DepartmentRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import com.fource.hrbank.repository.employee.EmployeeSpecification;
import com.fource.hrbank.service.changelog.ChangeLogService;
import com.fource.hrbank.service.storage.FileStorage;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    // 정렬 필드 상수
    public static final Set<String> VALID_SORT_FIELDS = Set.of(
        "name", "employeeNumber", "hireDate"
    );
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
        Department department = findDepartmentById(request.departmentId());
        FileMetadata profile = handleProfileImageUpload(profileImage);
        String employeeNumber = generateEmployeeNumber();

        Employee employee = createEmployeeEntity(request, department, profile, employeeNumber);
        Employee savedEmployee = employeeRepository.save(employee);

        List<DiffsDto> diffs = changeLogService.createEmployeeDiffs(null, savedEmployee);
        changeLogService.create(savedEmployee.getEmployeeNumber(), ChangeType.CREATED,
            request.memo(), diffs);

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
    public CursorPageResponse<EmployeeDto> findAll(String nameOrEmail, String employeeNumber,
        String departmentName,
        String position, EmployeeStatus status, LocalDate hireDateFrom, LocalDate hireDateTo,
        String sortField, String sortDirection,
        String cursor, Long idAfter, int size) {

        validateSortField(sortField);

        // 1. 정렬 방향
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection)
            .orElse(Sort.Direction.ASC);

        // 2. 페이징 정보
        Pageable pageable = PageRequest.of(0, size + 1,
            Sort.by(direction, sortField).and(Sort.by("id")));

        // 3. Specification 조합 (검색 조건 + 커서 조건)
        Specification<Employee> spec = buildSearchSpecification(
            nameOrEmail, departmentName, position, employeeNumber, status, hireDateFrom, hireDateTo,
            sortField, cursor, idAfter);

        // 4. 데이터 조회
        List<Employee> employees = employeeRepository.findAll(spec, pageable).getContent();

        return buildCursorPageResponse(employees, size, sortField);
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

        Employee employee = findEmployeeById(id);
        validateEmailDuplication(request.email(), employee.getEmail());
        Department department = findDepartmentById(request.departmentId());
        FileMetadata profile = updateProfileImageIfPresent(employee, profileImage);

        // 변경사항 감지 및 이력 저장
        Employee afterEmployee = createUpdatedEmployee(employee, request, department, profile);
        List<DiffsDto> diffs = changeLogService.createEmployeeDiffs(employee, afterEmployee);

        changeLogService.create(employee.getEmployeeNumber(), ChangeType.UPDATED, request.memo(),
            diffs);

        //실제 업데이트
        employee.update(request.name(), request.email(), department,
            request.position(), request.hireDate(), request.status(), profile);

        return employeeMapper.toDto(employee);
    }

    /**
     * id로 직원 삭제, 프로필 이미지가 존재할 경우 함께 삭제 삭제 이력과 상세변경 내용도 함께 저장됨
     * <p>
     * 직원의 삭제 여부(deleted)를 true로 설정한 후 저장하고
     *
     * @param id 삭제할 직원의 고유 ID
     */
    @Transactional
    @Override
    public void deleteById(Long id) {
        Employee employee = findEmployeeById(id);

        List<DiffsDto> diffs = changeLogService.createEmployeeDiffs(employee, null);
        changeLogService.create(employee.getEmployeeNumber(), ChangeType.DELETED, "직원 삭제", diffs);

        employeeRepository.delete(employee);

        FileMetadata profile = employee.getProfile();
        if (profile != null) {
            try {
                // DB에서 메타데이터 삭제
                fileMetadataRepository.delete(profile);
                log.info("프로필 이미지 삭제 완료 - ID: {}", profile.getId());
            } catch (Exception e) {
                log.warn("프로필 이미지 삭제 실패: {}", e.getMessage());
            }
        } else {
            log.info("삭제할 프로필 이미지가 없습니다.");
        }


    }


    // ============= 검증 메서드 =============
    // 이메일 중복 검증
    private void validateEmailDuplication(String email, String currentEmail) {
        if (!email.equals(currentEmail) && employeeRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }

    // 부서 조회
    private Department findDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
            .orElseThrow(DepartmentNotFoundException::new);
    }

    // 직원 조회
    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(EmployeeNotFoundException::new);
    }

    // ============= 헬퍼 메서드 =============
    // 프로필 이미지 업로드 처리
    private FileMetadata handleProfileImageUpload(Optional<MultipartFile> profileImage) {
        if (profileImage.isEmpty() || profileImage.get().isEmpty()) {
            return null;
        }

        MultipartFile file = profileImage.get();
        log.info("프로필 이미지 저장 - name: {}", file.getOriginalFilename());

        FileMetadata metadata = new FileMetadata(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize()
        );
        FileMetadata savedMetadata = fileMetadataRepository.save(metadata);

        try {
            fileStorage.put(savedMetadata.getId(), file.getBytes());
            return savedMetadata;
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new FileIOException(ResponseMessage.FILE_SAVE_ERROR_MESSAGE,
                ResponseDetails.FILE_SAVE_ERROR_MESSAGE);
        }
    }

    // 사원 번호 생성, 예시 : EMP-2025-158861485055084
    private String generateEmployeeNumber() {
        return String.format("EMP-%d-%d", Year.now().getValue(), System.currentTimeMillis());
    }

    // 직원 엔티티 생성
    private Employee createEmployeeEntity(EmployeeCreateRequest request, Department department,
        FileMetadata profile, String employeeNumber) {

        return new Employee(
            profile, department, request.name(), request.email(), employeeNumber,
            request.position(), request.hireDate(), EmployeeStatus.ACTIVE, null
        );
    }

    // 정렬 필드 유효성 검증
    private void validateSortField(String sortField) {
        if (sortField != null && !VALID_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다: " + sortField);
        }
    }

    // 검색 조건 구성
    private Specification<Employee> buildSearchSpecification(String nameOrEmail,
        String departmentName,
        String position, String employeeNumber, EmployeeStatus status, LocalDate hireDateFrom,
        LocalDate hireDateTo, String sortField, String cursor, Long idAfter) {
        return Specification
            .where(EmployeeSpecification.nameOrEmailLike(nameOrEmail))
            .and(EmployeeSpecification.departmentContains(departmentName))
            .and(EmployeeSpecification.positionContains(position))
            .and(EmployeeSpecification.employeeNumber(employeeNumber))
            .and(EmployeeSpecification.statusEquals(status))
            .and(EmployeeSpecification.hireDateFrom(hireDateFrom))
            .and(EmployeeSpecification.hireDateTo(hireDateTo))
            .and(EmployeeSpecification.buildCursorSpec(sortField, cursor, idAfter));
    }

    // 커서 페이지 응답 구성
    private CursorPageResponse<EmployeeDto> buildCursorPageResponse(List<Employee> employees,
        int size, String sortField) {
        boolean hasNext = employees.size() > size;

        List<EmployeeDto> content = employees.stream()
            .limit(size)
            .map(employeeMapper::toDto)
            .toList();

        String nextCursor =
            hasNext ? extractCursorValue(sortField, content.get(content.size() - 1)) : null;
        Long nextId = hasNext ? content.get(content.size() - 1).id() : null;
        long totalElements = employeeRepository.count();

        return new CursorPageResponse<>(content, nextCursor, nextId, size, totalElements, hasNext);
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

    // 프로필 이미지 업데이트
    private FileMetadata updateProfileImageIfPresent(Employee employee,
        Optional<MultipartFile> profileImage) {
        if (profileImage.isPresent() && !profileImage.get().isEmpty()) {
            return handleProfileImageUpload(profileImage);
        }
        return employee.getProfile(); // 기존 프로필 유지
    }

    // 업데이트 된 직원 객체 생성
    private Employee createUpdatedEmployee(Employee original, EmployeeUpdateRequest request,
        Department department, FileMetadata profile) {
        return new Employee(
            profile,
            department,
            request.name(),
            request.email(),
            original.getEmployeeNumber(),
            request.position(),
            request.hireDate(),
            request.status(),
            Instant.now()
        );
    }

    @Override
    public long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        return employeeRepository.countByFilters(status, fromDate, toDate);
    }
}
