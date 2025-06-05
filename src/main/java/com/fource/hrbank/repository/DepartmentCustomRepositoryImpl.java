package com.fource.hrbank.repository;

import com.fource.hrbank.domain.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DepartmentCustomRepositoryImpl implements DepartmentCustomRepository {

    private final EntityManager em;
    @Override
    public List<Department> findByCursorCondition(String keyword, Long lastId, String cursorValue, int size, String sortField, String sortDirection) {
        StringBuilder sql = new StringBuilder("SELECT d FROM Department d WHERE 1=1");

        // 검색 키워드가 있을 경우 부분 일치 조건 추가
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (d.name LIKE :keyword OR d.description LIKE :keyword)");
        }

        // 커서 조건 추가
        if ("name".equals(sortField)) {
            sql.append(" AND (d.name ");
            sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :cursor" : "< :cursor");
            sql.append(" OR (d.name = :cursor AND d.id ");
            sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :lastId" : "< :lastId))");
        } else if ("establishedDate".equals(sortField)) {
            sql.append(" AND (d.establishedDate ");
            sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :cursor" : "< :cursor");
            sql.append(" OR (d.establishedDate = :cursor AND d.id ");
            sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :lastId" : "< :lastId))");
        }

        // 정렬
        sql.append(" ORDER BY d.")
                .append(sortField).append(" ").append(sortDirection.toUpperCase())
                .append(", d.id ").append(sortDirection.toUpperCase());

        TypedQuery<Department> query = em.createQuery(sql.toString(), Department.class);

        // 파라미터 바인딩
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (cursorValue != null && lastId != null) {
            if ("name".equals(sortField)) {
                query.setParameter("cursor", cursorValue);
            } else if ("establishedDate".equals(sortField)) {
                query.setParameter("cursor", LocalDate.parse(cursorValue)); // 문자열로 받았을 경우 파싱
            }
            query.setParameter("lastId", lastId);
        }
        return query.setMaxResults(size).getResultList();
    }
}
