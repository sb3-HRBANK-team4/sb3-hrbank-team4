package com.fource.hrbank.repository;

import com.fource.hrbank.domain.ChangeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeDetailRepository extends JpaRepository<ChangeDetail, Long> {

}