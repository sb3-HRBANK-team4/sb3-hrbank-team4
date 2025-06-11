package com.fource.hrbank.service.department;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.Department;
import com.fource.hrbank.dto.common.CursorPageResponse;
import com.fource.hrbank.dto.department.DepartmentCreateRequest;
import com.fource.hrbank.dto.department.DepartmentDto;
import com.fource.hrbank.dto.department.DepartmentUpdateRequest;
import com.fource.hrbank.exception.DepartmentDeleteException;
import com.fource.hrbank.exception.DepartmentNotFoundException;
import com.fource.hrbank.exception.DuplicateDepartmentException;
import com.fource.hrbank.mapper.DepartmentMapper;
import com.fource.hrbank.repository.department.DepartmentRepository;
import com.fource.hrbank.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 부서 관련 비즈니스 로직을 당담하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * 부서 목록을 조회합니다.
     *
     * @param nameOrDescription 부서명 또는 부서 설명에 대한 검색어 (부분 일치)
     * @param idAfter           마지막 요소의 ID (동일 정렬 필드일 경우 tie-breaker 역할)
     * @param cursor            현재 커서 위치의 정렬 기준 값 (예: 부서명 또는 설립일)
     * @param size              조회할 데이터 개수
     * @param sortField         정렬 기준 필드 (예: "name", "establishedDate")
     * @param sortDirection     정렬 방향 ("ASC" 또는 "DESC")
     * @return 조건에 부합하는 부서 목록
     */
    @Transactional(readOnly = true)
    @Override
    public CursorPageResponse<DepartmentDto> findAll(String nameOrDescription, Long idAfter,
        String cursor, int size, String sortField, String sortDirection) {

        // size + 1 만큼 select -> hasNext 판단
        List<Department> departments = departmentRepository.findByCursorCondition(nameOrDescription,
            idAfter, cursor, size, sortField, sortDirection);

        // 실제 보여질 부서 데이터
        List<Department> content =
            departments.size() > size ? departments.subList(0, size) : departments;

        // cursor, id 설정
        String nextCursor = content.isEmpty() ? null
            : extractCursorValue(content.get(content.size() - 1), sortField);
        Long nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).getId();

        // 다음 페이지 존재 여부 판단
        boolean hasNext = departments.size() > size;

        // 부서 전체 갯수 설정
        long totalCount = departmentRepository.countByKeyword(nameOrDescription);

        List<Long> departmentIds = content.stream().map(Department::getId).toList();

        // Map(departmentId, employeeCount)
        Map<Long, Long> departmentIdAndEmployeeCount = departmentRepository.countByDepartmentIds(departmentIds);

        return new CursorPageResponse<DepartmentDto>(
            content.stream().map(department -> {
                Long employeeCount = departmentIdAndEmployeeCount.getOrDefault(department.getId(), 0L);
                return departmentMapper.toDto(department, employeeCount);
            }).toList(),
            nextCursor,
            nextIdAfter,
            size,
            totalCount,
            hasNext
        );
    }

    /**
     * @param request 부서 생성 정보를 담은 DTO
     * @return 생성한 부서 정보
     */
    @Override
    public DepartmentDto create(DepartmentCreateRequest request) {
        Department department = new Department(
            request.getName(),
            request.getDescription(),
            request.getEstablishedDate(),
            Instant.now()
        );

        return departmentMapper.toDto(departmentRepository.save(department), null);
    }

    /**
     * @param departmentId 수정할 부서의 ID
     * @param request 부서 수정 정보를 담은 DTO(name, description, establishedDate)
     * @return 수정한 부서 정보
     */
    @Override
    public DepartmentDto update(Long departmentId, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(DepartmentNotFoundException::new);

        if (request.getName() != null && !request.getName().equals(department.getName())) {
            if (departmentRepository.existsByName(request.getName())) {
                throw new DuplicateDepartmentException();
            }
        }

        department.update(request);

        return departmentMapper.toDto(department, null);
    }

    @Override
    public void delete(Long departmentId) {
        if (departmentId == null) throw new IllegalArgumentException("부서 코드는 필수입니다.");

        if (employeeRepository.existsByDepartmentId(departmentId)) throw new DepartmentDeleteException();

        if (!departmentRepository.existsById(departmentId)) throw new DepartmentNotFoundException();

        departmentRepository.deleteById(departmentId);
    }

    /**
     * @param department 부서 엔티티
     * @param sortField  정렬 기준 필드 (예: "name", "establishedDate")
     * @return 정렬 필드 기준 데이터 (예: "서비스 개발", "2025-03-02")
     */
    private String extractCursorValue(Department department, String sortField) {
        return switch (sortField) {
            case "name" -> department.getName();
            case "establishedDate" -> department.getEstablishedDate().toString();
            default -> null;
        };
    }

}
