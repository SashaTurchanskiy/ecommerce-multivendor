package com.alek.service;

import com.alek.model.Order;
import com.alek.model.Seller;
import com.alek.model.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Order order);
    List<Transaction> getTransactionsBySellerId(Seller seller);
    List<Transaction> getAllTransaction();
}
