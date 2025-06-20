package com.fource.hrbank.domain;

import com.fource.hrbank.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_change_detail")
public class ChangeDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_log_id")
    private ChangeLog changeLog;

    @Column(name = "field_name")
    private String propertyName;

    @Column(name = "old_value")
    private String before;

    @Column(name = "new_value")
    private String after;
}
