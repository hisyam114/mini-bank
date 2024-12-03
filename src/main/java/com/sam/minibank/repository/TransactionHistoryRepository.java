package com.sam.minibank.repository;

import com.sam.minibank.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    // Method to find all transactions for a specific CIF number
    List<TransactionHistory> findByCif(Long cif);

}