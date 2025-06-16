package com.bankapplication.controller;

import com.bankapplication.dto.*;
import com.bankapplication.service.impl.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest)
    {
        return userService.createAccount(userRequest);
    }
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request)
    {
        return userService.balanceEnquiry(request);
    }
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request)
    {
        return userService.nameEnquiry(request);
    }
    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request)
    {
        return userService.creditAccount(request);
    }
    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request)
    {
        return userService.debitAccount(request);
    }
    @PostMapping("/transfer")
    public BankResponse transferAmount(@RequestBody TransferRequest request)
    {
        return userService.transferAmount(request);
    }
    @GetMapping("/accounts/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactionHistory(@PathVariable String accountNumber) {
        try {
            List<TransactionDto> transactions = userService.transactionHistory(
                    TransactionDto.builder().accountNumber(accountNumber).build()
            );
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
