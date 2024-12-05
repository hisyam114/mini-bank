package com.sam.minibank.repository;

import com.sam.minibank.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
@Service
public interface BankRepository extends JpaRepository<Bank, Long> {
    List<Bank> findByNikAndType(String nik, String type);
    List<Bank> findByName(String name);
    List<Bank> findByProdName(String prodName);
    Bank findByCif(Long cif);
}

