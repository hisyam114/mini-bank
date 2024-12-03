package com.sam.minibank.repository;

import com.sam.minibank.model.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    // You can add custom queries here if necessary.
    // For example, checking if an account exists:
    boolean existsByCif(Long cif);
}
