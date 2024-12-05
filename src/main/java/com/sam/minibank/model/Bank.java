package com.sam.minibank.model;

import jakarta.persistence.*;
import java.util.Date;
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
    private Date year;

    private String nik;  // NIK - Identification Number
    private String telpNo;  // TELP_NO - Phone Number
    private String motherName;  // MOTHER_NAME - Name of the Mother

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;  // Reference to the Product (Deposito or Saving)

    public Bank() {
    }

    public Bank(String prodName, String name, String type, String branch, Long amount, Date year, String nik, String telpNo, String motherName) {
        this.prodName = prodName;
        this.name = name;
        this.type = type;
        this.branch = branch;
        this.amount = amount;
        this.year = year;
        this.nik = nik;
        this.telpNo = telpNo;
        this.motherName = motherName;
    }
}
