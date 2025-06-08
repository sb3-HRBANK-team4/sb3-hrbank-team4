package com.fource.hrbank.domain;

import com.fource.hrbank.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;


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
