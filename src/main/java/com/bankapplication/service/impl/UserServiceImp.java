package com.bankapplication.service.impl;

import com.bankapplication.dto.*;
import com.bankapplication.entity.Transaction;
import com.bankapplication.entity.User;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.repository.UserRepository;
import com.bankapplication.utils.AccountUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode("001")
                    .responseMessage("User with this email already exists!")
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstname(userRequest.getFirstname())
                .lastname(userRequest.getLastname())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .state(userRequest.getState())
                .country(userRequest.getCountry())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .accountNumber(AccountUtils.accountNumber())
                .accountBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        return BankResponse.builder()
                .responseCode("002")
                .responseMessage("Account created successfully!")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(String.valueOf(savedUser.getAccountBalance()))
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstname() + " " + savedUser.getLastname())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("Account Number does not exist!")
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode("004")
                .responseMessage("Account Number exists!")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(String.valueOf(foundUser.getAccountBalance()))
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstname() + " " + foundUser.getLastname())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist)
        {
            return "Account Number Is not Exist !";
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstname() + " " + foundUser.getLastname();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("Account Number does not exist!")
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .status("SUCCESS")
                .build();
        transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
                .responseMessage("Amount Credited Successful!")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(String.valueOf(userToCredit.getAccountBalance()))
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountName(userToCredit.getFirstname() + " " + userToCredit.getLastname())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("Account Number does not exist!")
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

        BigDecimal availableBalance = userToDebit.getAccountBalance();
        BigDecimal debitAmount = request.getAmount();

        if (availableBalance.compareTo(debitAmount) < 0) {
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("Insufficient Balance")
                    .accountInfo(null)
                    .build();
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToDebit.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .status("SUCCESS")
                .build();
        transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
                .responseMessage("Amount Debited Successful!")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(String.valueOf(userToDebit.getAccountBalance()))
                        .accountNumber(userToDebit.getAccountNumber())
                        .accountName(userToDebit.getFirstname() + " " + userToDebit.getLastname())
                        .build())
                .build();
    }

    @Override
    public BankResponse transferAmount(TransferRequest request) {
        boolean isSourceAccountExists = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExists)
        {
            return BankResponse.builder()
                    .responseCode("005")
                    .responseMessage("Destination Account Number does not exist!")
                    .accountInfo(null)
                    .build();
        }
        if(!isSourceAccountExists)
        {
            return BankResponse.builder()
                    .responseCode("006")
                    .responseMessage(" Source Account Number does not exist!")
                    .accountInfo(null)
                    .build();
        }
        User sourceUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceUser.getAccountBalance())> 0){
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("Insufficient Balance")
                    .accountInfo(null)
                    .build();
        }
        sourceUser.setAccountBalance(sourceUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceUser);
        User destinationUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationUser.setAccountBalance(destinationUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationUser);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(sourceUser.getAccountNumber())
                .transactionType("Transfer")
                .amount(request.getAmount())
                .destinAccountNumber(destinationUser.getAccountNumber())
                .status("SUCCESS")
                .build();
        transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
                .responseMessage("Amount transferred Successful!")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(String.valueOf(sourceUser.getAccountBalance()))
                        .accountNumber(sourceUser.getAccountNumber())
                        .accountName(sourceUser.getFirstname() + " " + sourceUser.getLastname())
                        .build())
                .build();
    }

    @Override
    public List<TransactionDto> transactionHistory(TransactionDto request) {
        boolean isAccountExist = transactionRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return Collections.emptyList(); // Return empty list instead of null DTO
        }

        List<Transaction> transactions = transactionRepository.findAllByAccountNumber(request.getAccountNumber());
        return transactions.stream()
                .map(transaction -> TransactionDto.builder()
                        .accountNumber(transaction.getAccountNumber())
                        .transactionType(transaction.getTransactionType())
                        .destinAccountNumber(transaction.getDestinAccountNumber())
                        .amount(transaction.getAmount())
                        .status(transaction.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}