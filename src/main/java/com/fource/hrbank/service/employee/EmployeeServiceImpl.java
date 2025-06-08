package com.fource.hrbank.service.employee;

import com.fource.hrbank.domain.*;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeCreateRequest;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeUpdateRequest;
import com.fource.hrbank.exception.DuplicateEmailException;
import com.fource.hrbank.exception.EmployeeNotFoundException;
import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.mapper.EmployeeMapper;
import com.fource.hrbank.repository.*;
import com.fource.hrbank.service.storage.FileStorage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 직원 관련 비즈니스 로직을 당담하는 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;
    private final FileStorage fileStorage;
    private final FileMetadataRepository fileMetadataRepository;
    private final ChangeLogRepository changeLogRepository;

    /**
     * @param request      직원 생성 요청 정보
     * @param profileImage 프로필 이미지 (선택)
     * @return 생성된 직원 정보 DTO
     */
    @Transactional
    @Override
    public EmployeeDto create(EmployeeCreateRequest request, Optional<MultipartFile> profileImage) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("이미 등록된 이메일: " + request.email());
        }

        FileMetadata profile = null;

        // 프로필 이미지 저장 처리
        if (profileImage.isPresent() && !profileImage.get().isEmpty()) {
            MultipartFile file = profileImage.get();

            // 메타정보 생성 및 저장
            FileMetadata metadata = new FileMetadata(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
            FileMetadata savedMetadata = fileMetadataRepository.save(metadata);

            // 바이트 저장
            try {
                fileStorage.put(savedMetadata.getId(), file.getBytes());
                profile = savedMetadata;
            } catch (IOException e) {
                throw new FileIOException(FileIOException.FILE_SAVE_ERROR_MESSAGE, e);
            }
        }

        // 부서 조회
        Department department = departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서 ID입니다: " + request.departmentId()));

        //사원번호 예시 : EMP-2025-158861485055084
        String employeeNumber = String.format("EMP-" + Year.now().getValue() + "-" + System.currentTimeMillis());

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

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));
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
    public CursorPageResponseEmployeeDto findAll(String nameOrEmail, String departmentName,
                                                 String position, EmployeeStatus status, String sortField, String sortDirection,
                                                 String cursor, Long idAfter, int size) {

        // 1. 정렬 방향
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);

        // 2. 페이징 정보
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(direction, sortField).and(Sort.by("id")));

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
        String nextCursor = hasNext ? extractCursorValue(sortField, content.get(content.size() - 1)) : null;
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

    @Transactional
    @Override
    public EmployeeDto update(Long id, EmployeeUpdateRequest request, Optional<MultipartFile> profileImage) {
        //1. 수정할 엔티티 조회
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));

        //2. email 중복 체크
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("이미 등록된 이메일: " + request.email());
        }

        Department department = departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new EntityNotFoundException("부서가 존재하지 않습니다."));

        FileMetadata profile = Optional.of(profileImage);
        // 프로필 이미지 저장 처리
        if (profileImage.isPresent() && !profileImage.get().isEmpty()) {
            MultipartFile file = profileImage.get();

            // 메타정보 생성 및 저장
            FileMetadata metadata = new FileMetadata(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
            FileMetadata savedMetadata = fileMetadataRepository.save(metadata);

            // 바이트 저장
            try {
                fileStorage.put(savedMetadata.getId(), file.getBytes());
                profile = savedMetadata;
            } catch (IOException e) {
                throw new FileIOException(FileIOException.FILE_SAVE_ERROR_MESSAGE, e);
            }
        }

        employee.update(
            request.name(),
            request.email(),
            department,
            request.position(),
            request.hireDate(),
            request.status(),
            profile
        );


        // 변경 로그 저장
        changeLogService.saveChangeLog(
            employee,
            ChangeType.UPDATED,
            request.memo()
        );
        
        changeLogRepository.save(changeLog);

        return employeeMapper.toDto(employee);
    }
}
