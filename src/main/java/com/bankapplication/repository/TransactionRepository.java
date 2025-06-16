package com.bankapplication.repository;

import com.bankapplication.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
    List<Transaction> findAllByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
