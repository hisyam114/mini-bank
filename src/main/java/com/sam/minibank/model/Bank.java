package com.sam.minibank.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

@Table(name = "ledger")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cif;

    private String prodName;
    private String name;
    private String type;
    private String branch;
    private Long amount;
    private int year;

    public Bank() {
    }

    public Bank(String prodName, String name, String type, String branch, Long amount, int year) {
        this.prodName = prodName;
        this.name = name;
        this.type = type;
        this.branch = branch;
        this.amount = amount;
        this.year = year;
    }
}
