package com.sam.minibank.repository;

import com.sam.minibank.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);  // Find products by their name
    List<Product> findByType(String type);  // Find products by their type (deposit or saving)
}

