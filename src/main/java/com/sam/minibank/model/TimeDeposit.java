package com.sam.minibank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Entity
@Data
@Table(name = "time_deposit")
public class TimeDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cif; // linked to customer account (Bank account's CIF)
    private Long amount; // amount deposited
    private Double interestRate; // interest rate for the time deposit
    private LocalDate depositStartDate; // date when the deposit is made
    private LocalDate depositEndDate; // date when the deposit matures
    private Long maturityAmount; // amount after interest
    private String status; // Status field: "A" for Active, "M" for Mature, "O" for Other


    @ManyToOne
    @JoinColumn(name = "cif", referencedColumnName = "cif", insertable = false, updatable = false)
    private Bank bank; // Linking to the Bank account via CIF (optional)

    // No-argument constructor (required by JPA)
    public TimeDeposit() {
    }

    // Constructors, getters, and setters
    public TimeDeposit(Long cif, Long amount, Double interestRate, LocalDate depositStartDate, LocalDate depositEndDate) {
        this.cif = cif;
        this.amount = amount;
        this.interestRate = interestRate;
        this.depositStartDate = depositStartDate;
        this.depositEndDate = depositEndDate;
        this.maturityAmount = calculateMaturityAmount();
    }

    public Long calculateMaturityAmount() {
        long daysBetween = ChronoUnit.DAYS.between(depositStartDate, depositEndDate);
        // Simple interest calculation (you can replace this with more sophisticated formulas)
        double interest = amount * interestRate * daysBetween / 365; // interest based on days
        return amount + (long) interest;
    }

    // Determine the status based on the current date
    public String determineStatus() {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(depositStartDate)) {
            return "O";  // "Other" - Before deposit starts
        } else if (currentDate.isAfter(depositEndDate)) {
            return "M";  // "Mature" - After deposit end date
        } else {
            return "A";  // "Active" - Within the deposit period
        }
    }
}

