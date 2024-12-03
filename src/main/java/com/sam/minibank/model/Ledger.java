package com.sam.minibank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Ledger {

    @Id
    private Long cif;  // Customer Identification Number for the ledger

    private Long amount;  // Account balance in the ledger

    // Constructors, getters, and setters
    public Ledger() {
    }

    public Ledger(Long cif, Long amount) {
        this.cif = cif;
        this.amount = amount;
    }

    public Long getCif() {
        return cif;
    }

    public void setCif(Long cif) {
        this.cif = cif;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}

