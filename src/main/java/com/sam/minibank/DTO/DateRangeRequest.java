package com.sam.minibank.DTO;

import java.util.Date;

public class DateRangeRequest {

    private Long cif;
    private Date startDate;
    private Date endDate;

    // Default constructor
    public DateRangeRequest() {}

    // Constructor with cifNo, startDate, and endDate
    public DateRangeRequest(Long cif, Date startDate, Date endDate) {
        this.cif = cif;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter and Setter methods
    public Long getCifNo() {
        return cif;
    }

    public void setCifNo(Long cif) {
        this.cif = cif;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

