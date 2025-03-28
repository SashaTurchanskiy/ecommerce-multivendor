package com.alek.repository;

import com.alek.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySellerId(Long sellerId);
}
