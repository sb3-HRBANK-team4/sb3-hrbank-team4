package com.fource.hrbank.repository.employee;

import com.fource.hrbank.domain.Employee;
import com.fource.hrbank.domain.EmployeeStatus;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

/**
 * 동적 where 조건을 표현하는 클래스 이름 또는 이메일 : like, or 조건 부서명 : like 직함 : like 상태 : 정확히 일치(equal)
 */
public class EmployeeSpecification {

    public static Specification<Employee> nameOrEmailLike(String nameOrEmail) {
        return (root, query, cb) -> {
            if (nameOrEmail == null || nameOrEmail.isBlank()) {
                return null;
            }
            String like = "%" + nameOrEmail + "%";
            return cb.or(
                cb.like(root.get("name"), like),
                cb.like(root.get("email"), like)
            );
        };
    }

    public static Specification<Employee> departmentContains(String department) {
        return (root, query, cb) -> {
            if (department == null || department.isBlank()) {
                return null;
            }
            return cb.like(root.join("department").get("name"), "%" + department + "%");
        };
    }

    public static Specification<Employee> positionContains(String position) {
        return (root, query, cb) -> {
            if (position == null || position.isBlank()) {
                return null;
            }
            return cb.like(root.get("position"), "%" + position + "%");
        };
    }

    public static Specification<Employee> statusEquals(EmployeeStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * 커서 기반 페이지네이션을 위한 조건을 생성합니다. 정렬 필드와 커서 값, 마지막 ID를 기반으로 다음 페이지의 데이터를 필터링합니다.
     *
     * @param sortField 정렬 기준 필드명 ("name", "employeeNumber", "hireDate")
     * @param cursor    커서 값 (마지막 요소의 정렬 기준 값)
     * @param idAfter   마지막 요소의 ID
     * @return 커서 기반 조건을 적용한 Specification
     */
    public static Specification<Employee> buildCursorSpec(String sortField, String cursor,
        Long idAfter) {
        return (root, query, cb) -> {
            Path path = root.get(sortField);

            Comparable cursorValue = convertToComparable(sortField, cursor);
            if (cursorValue == null) {
                return null;
            }

            Predicate greater = cb.greaterThan(path, cursorValue);
            Predicate equalAndId = cb.and(
                cb.equal(path, cursorValue),
                cb.greaterThan(root.get("id"), idAfter)
            );

            return cb.or(greater, equalAndId);
        };
    }

    // hireDate의 경우 date형식이 필요함으로 비교 메서드
    private static Comparable<?> convertToComparable(String field, String cursor) {
        try {
            return switch (field) {
                case "hireDate" -> LocalDate.parse(cursor);
                case "employeeNumber" -> cursor;
                case "name" -> cursor;
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }
}
