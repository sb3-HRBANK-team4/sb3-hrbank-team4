package com.fource.hrbank.repository;

import com.fource.hrbank.domain.ChangeLog;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    boolean findByChangedAtAfter(Instant changedAtAfter);

    boolean existsByChangedAtAfter(Instant changedAtAfter);
}