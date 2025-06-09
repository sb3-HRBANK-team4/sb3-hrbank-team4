package com.fource.hrbank.repository;

import com.fource.hrbank.domain.ChangeDetail;
import com.fource.hrbank.domain.ChangeLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeDetailRepository extends JpaRepository<ChangeDetail, Long> {
    List<ChangeDetail> findByChangeLog(ChangeLog changeLog);
}