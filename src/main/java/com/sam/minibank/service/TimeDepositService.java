package com.sam.minibank.service;

import com.sam.minibank.model.Bank;
import com.sam.minibank.model.TimeDeposit;
import com.sam.minibank.repository.BankRepository;
import com.sam.minibank.repository.TimeDepositRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Data
@Service
public class TimeDepositService {

    private final TimeDepositRepository timeDepositRepository;
    private final BankRepository bankRepository; // To access Bank account and ensure valid CIF

    @Autowired
    public TimeDepositService(TimeDepositRepository timeDepositRepository, BankRepository bankRepository) {
        this.timeDepositRepository = timeDepositRepository;
        this.bankRepository = bankRepository;
    }

    public TimeDeposit createTimeDeposit(Long cif, Long amount, Double interestRate, LocalDate depositStartDate, LocalDate depositEndDate) {
        // First, check if the bank account exists for the given CIF
        Bank account = bankRepository.findByCif(cif);
        if (account == null) {
            throw new RuntimeException("Account with CIF " + cif + " does not exist.");
        }

        // Create a new TimeDeposit and save it
        TimeDeposit timeDeposit = new TimeDeposit(cif, amount, interestRate, depositStartDate, depositEndDate);
        return timeDepositRepository.save(timeDeposit);
    }

    public List<TimeDeposit> getTimeDepositsByCif(Long cif) {
        return timeDepositRepository.findByCif(cif);
    }

    public TimeDeposit getTimeDepositById(Long id) {
        return timeDepositRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Time deposit not found with ID: " + id));
    }

    public Long calculateMaturityAmount(Long id) {
        TimeDeposit timeDeposit = getTimeDepositById(id);
        return timeDeposit.calculateMaturityAmount();
    }

    // Modified: Method to check if time deposits are matured based on CIF
    public Map<String, Object> checkMaturityStatusByCif(Long cif) {
        // Fetch all time deposits for the given CIF
        List<TimeDeposit> timeDeposits = timeDepositRepository.findByCif(cif);

        if (timeDeposits.isEmpty()) {
            return null;  // If no deposits exist for the given CIF, return null (indicating no time deposits found)
        }

        // Check maturity status for the first time deposit and return as a map with CIF and status
        Map<String, Object> response = new HashMap<>();
        response.put("cif", cif);

        // If any time deposit is matured, set status as "Mature"
        boolean isMatured = false;
        for (TimeDeposit deposit : timeDeposits) {
            if (LocalDate.now().isAfter(deposit.getDepositEndDate())) {
                isMatured = true;
                break;  // Stop checking once we find a matured deposit
            }
        }

        response.put("status", isMatured ? "Mature" : "Active");
        return response;
    }

    // Method to update the status of all time deposits
    public void updateTimeDepositStatuses() {
        List<TimeDeposit> timeDeposits = timeDepositRepository.findAll();
        for (TimeDeposit timeDeposit : timeDeposits) {
            String newStatus = timeDeposit.determineStatus();
            if (!newStatus.equals(timeDeposit.getStatus())) {
                timeDeposit.setStatus(newStatus);
                timeDepositRepository.save(timeDeposit);  // Save updated status
            }
        }
    }

    // Method to get all time deposits for a given CIF
    public List<TimeDeposit> getAllTimeDepositsByCif(Long cif) {
        return timeDepositRepository.findByCif(cif);
    }

}

