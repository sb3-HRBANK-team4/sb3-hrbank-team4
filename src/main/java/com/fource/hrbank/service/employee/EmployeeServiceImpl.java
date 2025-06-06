package com.fource.hrbank.service.employee;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import com.fource.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.fource.hrbank.dto.employee.EmployeeDto;
import com.fource.hrbank.mapper.EmployeeMapper;
import com.fource.hrbank.repository.EmployeeRepository;
import com.fource.hrbank.repository.EmployeeSpecification;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * 직원 관련 비즈니스 로직을 당담하는 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("직원 정보를 찾을 수 없습니다."));
        return employeeMapper.toDto(employee);
    }

    /**
     * 직원 목록을 조회합니다.
     *
     * @param nameOrEmail 직원 이름 또는 이메일 (부분 일치)
     * @param departmentName 부서명 (부분 일치)
     * @param position 직함 (부분 일치)
     * @param status 직원 상태 (정확히 일치: ACTIVE, ON_LEAVE, RESIGNED)
     * @param sortField 정렬 기준 필드 (예: "name", "employeeNumber", "hireDate")
     * @param sortDirection 정렬 방향 ("ASC" 또는 "DESC")
     * @param cursor 정렬 필드의 마지막 커서 값 (예: 마지막 직원의 이름, 사번, 입사일 등)
     * @param idAfter 마지막 요소의 ID (동일 정렬 필드일 경우 tie-breaker 역할)
     * @param size 조회할 데이터 개수
     * @return 조건에 부합하는 직원 목록 페이지 응답 DTO
     */
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
            case "hireDate" -> dto.hireDate().toInstant().toString();
            default -> null;
        };
    }
}
