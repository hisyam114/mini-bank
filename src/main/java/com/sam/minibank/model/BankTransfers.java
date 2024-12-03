package com.sam.minibank.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

@Table(name = "ledger")
public class BankTransfers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cif;
    private Long  cifTo;

    private String reason;
    private String type;
    private Long amount;

    public BankTransfers() {
    }

    public BankTransfers(Long cif, Long cifTo, String reason, String type, Long amount) {
        this.cif = cif;
        this.cifTo = cifTo;
        this.reason = reason;
        this.type = type;
        this.amount = amount;
    }
}
