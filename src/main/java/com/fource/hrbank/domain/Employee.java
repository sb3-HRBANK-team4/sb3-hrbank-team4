package com.fource.hrbank.domain;

import com.fource.hrbank.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_employees")
public class Employee extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private FileMetadata profile;

    @Setter
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "employee_number")
    private String employeeNumber;

    @Column(name = "position")
    private String position;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public void update(String newName, String newEmail, Department newDepartment,
        String newPosition, LocalDate newHireDate,
        EmployeeStatus newStatus, FileMetadata newProfile) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newDepartment != null && !newDepartment.equals(this.department)) {
            this.department = newDepartment;
        }
        if (newPosition != null && !newPosition.equals(this.position)) {
            this.position = newPosition;
        }
        if (newHireDate != null && !newHireDate.equals(this.hireDate)) {
            this.hireDate = newHireDate;
        }
        if (newStatus != null && !newStatus.equals(this.status)) {
            this.status = newStatus;
        }
        if (newProfile != null && !newProfile.equals(
            this.profile)) {
            this.profile = newProfile;
        }
    }
}
