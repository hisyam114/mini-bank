package com.sam.minibank.repository;

import com.sam.minibank.model.BankTransfers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
@Service
public interface BankTransfersRepository extends JpaRepository<BankTransfers, Long> {
    List<BankTransfers> findByCif(Long cif);
    List<BankTransfers> findByCifTo(Long cifTo);
}

