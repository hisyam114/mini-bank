package com.sam.minibank.repository;

import com.sam.minibank.model.TimeDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeDepositRepository extends JpaRepository<TimeDeposit, Long> {
    List<TimeDeposit> findByCif(Long cif);
}

