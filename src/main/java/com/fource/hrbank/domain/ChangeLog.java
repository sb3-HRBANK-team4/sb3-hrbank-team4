package com.fource.hrbank.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fource.hrbank.domain.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_change_log")
public class ChangeLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "employee_number")
    private String employeeNumber;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "changed_at")
    private Instant changedAt;

    @Column(name = "changed_ip")
    private String changedIp;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ChangeType type;

    @Column(name = "memo")
    private String memo;

    @OneToMany(mappedBy = "changeLog", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChangeDetail> changeDetailList;
}
