package com.bankapplication.service.impl;

import com.bankapplication.dto.TransactionDto;
import com.bankapplication.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);
}
