package com.sam.minibank.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;  // Description of the product
    private String productName;   // e.g., "Deposito", "Saving"
    private Double interestRate;  // Interest rate for Deposito
    private String type;          // Type: "Saving" or "Deposito"

    public Product() {}

    // Getters and setters
    public Product(String name, String type, String description, Double interestRate) {
        this.productName = name;
        this.type = type;
        this.description = description;
        this.interestRate = interestRate;
    }
}
