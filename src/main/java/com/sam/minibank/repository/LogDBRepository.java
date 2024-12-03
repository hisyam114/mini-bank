package com.sam.minibank.repository;

import com.sam.minibank.model.LogDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LogDBRepository extends JpaRepository<LogDB, Long> {
    // You can define custom queries if needed
    // Example: List<LogDB> findByCifNo(Long cifNo);
    // Find logs by CIF number
    List<LogDB> findByCifNo(Long cifNo);

    // Find logs by transaction type (e.g., "DEPOSIT", "WITHDRAWAL", etc.)
    List<LogDB> findByTrxType(String trxType);

    // Find logs for a specific CIF within a date range
    List<LogDB> findByCifNoAndLogDateBetween(Long cifNo, Date startDate, Date endDate);
}