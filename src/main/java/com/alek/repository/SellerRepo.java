package com.alek.repository;

import com.alek.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepo extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
}
