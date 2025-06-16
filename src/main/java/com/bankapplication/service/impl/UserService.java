package com.bankapplication.service.impl;

import com.bankapplication.dto.*;

import java.util.List;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transferAmount(TransferRequest request);
    public List<TransactionDto> transactionHistory(TransactionDto request);
}
