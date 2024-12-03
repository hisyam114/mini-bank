package com.sam.minibank.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeDepositRequest {
    private Long cif;
    private Long amount;
    private Double interestRate;
    private LocalDate depositStartDate;
    private LocalDate depositEndDate;

    // Getters and setters
}

