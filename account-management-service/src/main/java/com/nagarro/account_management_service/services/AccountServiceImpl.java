package com.nagarro.account_management_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nagarro.account_management_service.DTOs.AccountDetailsDto;
import com.nagarro.account_management_service.DTOs.CustomerDetailsDto;
import com.nagarro.account_management_service.DTOs.GetCustomerSuccessDto;
import com.nagarro.account_management_service.DTOs.TransactionDto;
import com.nagarro.account_management_service.DTOs.TransactionStatusDto;
import com.nagarro.account_management_service.entities.Account;
import com.nagarro.account_management_service.exceptions.AccountNotFoundException;
import com.nagarro.account_management_service.exceptions.CustomerAlreadyHasSameTypeAccountException;
import com.nagarro.account_management_service.exceptions.CustomerDetailsNotFetchedException;
import com.nagarro.account_management_service.exceptions.CustomerNotFoundException;
import com.nagarro.account_management_service.exceptions.InsufficientBalanceException;
import com.nagarro.account_management_service.exceptions.UnAuthorizedTransactionException;
import com.nagarro.account_management_service.repositories.AccountRepository;
import com.nagarro.account_management_service.utils.ApiCaller;

import feign.FeignException;

@Service
public class AccountServiceImpl implements AccountService {
	
	private AccountRepository accountRepository;
	private ApiCaller apiCaller;
	
	@Autowired
	public AccountServiceImpl(AccountRepository accountRepository,
			ApiCaller apiCaller) {
		this.accountRepository = accountRepository;
		this.apiCaller = apiCaller;
	}
	
	public Account createAccount(Account account) {
		// validate if the customer with given Id exists
		try {
			// if the customer is fetched successfully, there will be no exception
			this.apiCaller.getCustomerDetails(account.getCustomerId());
			
		} catch (FeignException.NotFound notFound) {
			throw new CustomerNotFoundException();
			
		} catch (FeignException.InternalServerError internalServerError) {
			throw new CustomerDetailsNotFetchedException();
		}
		
		try{
			// create account
			return accountRepository.save(account);
			
		} catch (DataIntegrityViolationException exception) {
			exception.printStackTrace();
			throw new CustomerAlreadyHasSameTypeAccountException();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}
	
	
	@Override
	public AccountDetailsDto getAccountDetails(int accountId) {
		// get account
		Account account = this.accountRepository.findById(accountId)
				.orElseThrow(()->new AccountNotFoundException() );
		
		// fetch customer details for this account
		CustomerDetailsDto customerDetailsDto = null;
		try {
			ResponseEntity<GetCustomerSuccessDto> response = this.apiCaller.getCustomerDetails(account.getCustomerId());
			GetCustomerSuccessDto getCustomerSuccessDto = response.getBody();
			customerDetailsDto = getCustomerSuccessDto.getData(); 
			
		} catch (FeignException.InternalServerError e) {
			throw new CustomerDetailsNotFetchedException();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
		
		// combine the account and customer details
		AccountDetailsDto accountDetailsDto = new AccountDetailsDto();
		accountDetailsDto.setAccountId(account.getId());
		accountDetailsDto.setType(account.getType());
		accountDetailsDto.setCustomerId(customerDetailsDto.getCustomerId());
		accountDetailsDto.setName(customerDetailsDto.getName());
		accountDetailsDto.setEmail(customerDetailsDto.getEmail());
		accountDetailsDto.setDob(customerDetailsDto.getDob());
		accountDetailsDto.setNationality(customerDetailsDto.getNationality());
		accountDetailsDto.setBalance(account.getBalance());
		accountDetailsDto.setStatus(account.getStatus());
		
		return accountDetailsDto;
	}
	
	@Override
	public TransactionStatusDto deposit(TransactionDto transactionDto) {
		
		// get the account
		Account account = this.accountRepository.findById(transactionDto.getAccountId())
				.orElseThrow(()-> new AccountNotFoundException());
		
		// validate if the customer with given Id exists
		try {
			// if the customer is fetched successfully, there will be no exception
			this.apiCaller.getCustomerDetails(transactionDto.getCustomerId());
			
		} catch (FeignException.NotFound notFound) {
			throw new CustomerNotFoundException();
			
		} catch (FeignException.InternalServerError internalServerError) {
			throw new CustomerDetailsNotFetchedException();
		}
			
		// check if the customer Id passed in request is the actual owner of this account
		if(account.getCustomerId()==transactionDto.getCustomerId()) {
			// deposit
			try {
				account.setBalance(account.getBalance()+transactionDto.getAmount());
				this.accountRepository.save(account);
				TransactionStatusDto transactionStatusDto = new TransactionStatusDto();
				transactionStatusDto.setStatus("success");
				transactionStatusDto.setBalance(account.getBalance());
				return transactionStatusDto;
				
			} catch (Exception exception) {
				exception.printStackTrace();
				throw exception;
			}
			
		} else {
			// unauthorized
			throw new UnAuthorizedTransactionException();
		}
	}

	@Override
	public TransactionStatusDto withdraw(TransactionDto transactionDto) {
		
		// get the account
		Account account = this.accountRepository.findById(transactionDto.getAccountId())
				.orElseThrow(()-> new AccountNotFoundException());
		
		// validate if the customer with given Id exists
		try {
			// if the customer is fetched successfully, there will be no exception
			this.apiCaller.getCustomerDetails(transactionDto.getCustomerId());
			
		} catch (FeignException.NotFound notFound) {
			throw new CustomerNotFoundException();
			
		} catch (FeignException.InternalServerError internalServerError) {
			throw new CustomerDetailsNotFetchedException();
		}
		
		// check if the customer Id passed in request is the actual owner of this account
		if(account.getCustomerId()==transactionDto.getCustomerId()) {
			// withdraw
			try {
				if(account.getBalance()>=transactionDto.getAmount())
				{
					account.setBalance(account.getBalance()-transactionDto.getAmount());
					this.accountRepository.save(account);
					TransactionStatusDto transactionStatusDto = new TransactionStatusDto();
					transactionStatusDto.setStatus("success");
					transactionStatusDto.setBalance(account.getBalance());
					return transactionStatusDto;
				} else {
					throw new InsufficientBalanceException();
				}
				
			} catch (Exception exception) {
				exception.printStackTrace();
				throw exception;
			}	
		} else {
			// unauthorized
			throw new UnAuthorizedTransactionException();
		}
	}
	

	@Override
	public void deleteAccount(int accountId) {
		try {
			// check if the account exists
			Account account = this.accountRepository.findById(accountId)
					.orElseThrow(()-> new AccountNotFoundException());
			// delete account
			this.accountRepository.delete(account);
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}
	
	@Override
	public void deleteAccountByCustomerId(int customerId) {
		try {
			this.accountRepository.deleteAllByCustomerId(customerId);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}
}
