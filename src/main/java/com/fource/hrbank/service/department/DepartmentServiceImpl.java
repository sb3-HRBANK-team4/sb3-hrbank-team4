package com.fource.hrbank.service.department;

import com.fource.hrbank.domain.Department;
import com.fource.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.fource.hrbank.mapper.DepartmentMapper;
import com.fource.hrbank.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 부서 관련 비즈니스 로직을 당담하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * 부서 목록을 조회합니다.
     *
     * @param nameOrDescription 부서명 또는 부서 설명에 대한 검색어 (부분 일치)
     * @param idAfter 마지막 요소의 ID (동일 정렬 필드일 경우 tie-breaker 역할)
     * @param cursor 현재 커서 위치의 정렬 기준 값 (예: 부서명 또는 설립일)
     * @param size 조회할 데이터 개수
     * @param sortField 정렬 기준 필드 (예: "name", "establishedDate")
     * @param sortDirection 정렬 방향 ("ASC" 또는 "DESC")
     * @return 조건에 부합하는 부서 목록
     */
    @Override
    public CursorPageResponseDepartmentDto findAll(String nameOrDescription, Long idAfter, String cursor, int size, String sortField, String sortDirection) {

        List<Department> departments = departmentRepository.findByCursorCondition(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);

        List<Department> content = departments.size() > size ? departments.subList(0, size) : departments;

        String nextCursor = content.isEmpty() ? null : extractCursorValue(content.get(content.size() - 1), sortField);
        Long nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).getId();

        boolean hasNext = departments.size() > size;

        long totalCount = departmentRepository.countByKeyword(nameOrDescription);

        // 추후 employeeRepository의 직원 집계 메소드 구현 시 추가 작업 예정
        return new CursorPageResponseDepartmentDto(
                content.stream().map(department -> departmentMapper.toDto(department, null)).toList(),
                nextCursor,
                nextIdAfter,
                size,
                totalCount,
                hasNext
        );
    }

    /**
     * @param department 부서 엔티티
     * @param sortField 정렬 기준 필드 (예: "name", "establishedDate")
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
