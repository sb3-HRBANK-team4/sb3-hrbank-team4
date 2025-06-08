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

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean useCursor = cursorValue != null && lastId != null;

        // 키워드 조건
        if (hasKeyword) {
            sql.append(" AND (d.name LIKE :keyword OR d.description LIKE :keyword)");
        }

        // 커서 조건
        if (useCursor) {
            if ("name".equals(sortField)) {
                sql.append(" AND (d.name ");
                sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :cursor" : "< :cursor");
                sql.append(" OR (d.name = :cursor AND d.id ");
                sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :lastId" : "< :lastId");
                sql.append("))");
            } else if ("establishedDate".equals(sortField)) {
                sql.append(" AND (d.establishedDate ");
                sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :cursor" : "< :cursor");
                sql.append(" OR (d.establishedDate = :cursor AND d.id ");
                sql.append("ASC".equalsIgnoreCase(sortDirection) ? "> :lastId" : "< :lastId");
                sql.append("))");
            }
        }

        // 정렬 조건
        sql.append(" ORDER BY d.")
            .append(sortField).append(" ").append(sortDirection.toUpperCase())
            .append(", d.id ").append(sortDirection.toUpperCase());

        TypedQuery<Department> query = em.createQuery(sql.toString(), Department.class);

        // 파라미터 바인딩
        if (hasKeyword) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (useCursor) {
            if ("name".equals(sortField)) {
                query.setParameter("cursor", cursorValue);
            } else if ("establishedDate".equals(sortField)) {
                query.setParameter("cursor", LocalDate.parse(cursorValue));
            }
            query.setParameter("lastId", lastId);
        }

        // 페이징 처리
        return query.setMaxResults(size + 1).getResultList();
    }

    @Override
    public long countByKeyword(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(d) FROM Department d WHERE 1=1");

        TypedQuery<Long> query;

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (d.name LIKE :keyword OR d.description LIKE :keyword)");
            query = em.createQuery(sql.toString(), Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
        } else {
            query = em.createQuery(sql.toString(), Long.class);
        }

        return query.getSingleResult();
    }
}
