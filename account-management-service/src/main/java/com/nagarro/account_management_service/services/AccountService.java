package com.nagarro.account_management_service.services;

import com.nagarro.account_management_service.DTOs.AccountDetailsDto;
import com.nagarro.account_management_service.DTOs.TransactionDto;
import com.nagarro.account_management_service.DTOs.TransactionStatusDto;
import com.nagarro.account_management_service.entities.Account;

public interface AccountService {
	Account createAccount(Account account);
	AccountDetailsDto getAccountDetails(int accountId);
	TransactionStatusDto deposit(TransactionDto transactionDto);
	TransactionStatusDto withdraw(TransactionDto transactionDto);
	void deleteAccount(int accountId);
	void deleteAccountByCustomerId(int customerId);
}