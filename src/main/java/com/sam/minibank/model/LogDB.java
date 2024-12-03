package com.sam.minibank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data

@Table(name = "logDB")
public class LogDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLog;    // Primary Key (Auto-generated)

    private Long cifNo;    // Sender or recipient CIF number
    private String logDesc; // Log description (message)
    private Date logDate;   // Date of log entry
    private String trxType;

    // Default constructor
    public LogDB() {
    }

    // Constructor with parameters
    public LogDB(Long cifNo, String logDesc, String trxType) {
        this.cifNo = cifNo;
        this.logDesc = logDesc;
        this.logDate = new Date();  // Set current timestamp
        this.trxType = trxType;
    }
}
