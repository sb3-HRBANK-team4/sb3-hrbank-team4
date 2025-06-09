package com.fource.hrbank.domain;

import com.fource.hrbank.domain.common.BaseEntity;
import com.fource.hrbank.dto.department.DepartmentUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_department")
public class Department extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public void update(DepartmentUpdateRequest request) {
        if (request.getName() != null && !request.getName().equals(name)) {
            this.name = request.getName();
            this.updatedAt = Instant.now();
        }
        if (request.getDescription() != null && !request.getDescription().equals(description)) {
            this.description = request.getDescription();
            this.updatedAt = Instant.now();
        }
        if (request.getEstablishedDate() != null) {
            this.establishedDate = request.getEstablishedDate();
            this.updatedAt = Instant.now();
        }
    }
}
