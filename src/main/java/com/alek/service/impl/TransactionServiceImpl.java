package com.alek.service.impl;

import com.alek.model.Order;
import com.alek.model.Seller;
import com.alek.model.Transaction;
import com.alek.repository.SellerRepo;
import com.alek.repository.TransactionRepo;
import com.alek.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final SellerRepo sellerRepo;

    public TransactionServiceImpl(TransactionRepo transactionRepo, SellerRepo sellerRepo) {
        this.transactionRepo = transactionRepo;
        this.sellerRepo = sellerRepo;
    }

    @Override
    public Transaction createTransaction(Order order) {
        Seller seller = sellerRepo.findById(order.getSellerId()).get();

        Transaction transaction = new Transaction();
        transaction.setSeller(seller);
        transaction.setCustomer(order.getUser());

        return transactionRepo.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsBySellerId(Seller seller) {
        return transactionRepo.findBySellerId(seller.getId());
    }

    @Override
    public List<Transaction> getAllTransaction() {
        return transactionRepo.findAll();
    }
}
