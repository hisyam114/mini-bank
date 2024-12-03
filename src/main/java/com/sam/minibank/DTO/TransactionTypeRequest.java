package com.sam.minibank.DTO;

import lombok.Data;

@Data

public class TransactionTypeRequest {

    private String trxType;

    // Default constructor
    public TransactionTypeRequest() {}

    // Constructor with trxType
    public TransactionTypeRequest(String trxType) {
        this.trxType = trxType;
    }

    // Getter and Setter methods
    public String getTrxType() {
        return trxType;
    }

    public void setTrxType(String trxType) {
        this.trxType = trxType;
    }
}

