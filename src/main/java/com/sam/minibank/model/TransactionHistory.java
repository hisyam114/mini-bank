package com.sam.minibank.model;

import com.sam.minibank.DTO.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data

@Table(name = "transaction_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cif;  // Account number (CIF)
    private Long amount;  // Transaction amount
    private TransactionType transactionType;  // Type of transaction (DEPOSIT, WITHDRAWAL, TRANSFER)
    private Date transactionDate;  // Date and time of the transaction

    // Default constructor
    public TransactionHistory() {}

    // Constructor with parameters
    public TransactionHistory(Long cif, Long amount, TransactionType transactionType) {
        this.cif = cif;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = new Date();  // Current date and time of the transaction
    }
}

