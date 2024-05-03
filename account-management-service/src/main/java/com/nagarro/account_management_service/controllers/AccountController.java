package com.nagarro.account_management_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.account_management_service.DTOs.AccountDetailsDto;
import com.nagarro.account_management_service.DTOs.ErrorResponse;
import com.nagarro.account_management_service.DTOs.SuccessResponse;
import com.nagarro.account_management_service.DTOs.TransactionDto;
import com.nagarro.account_management_service.DTOs.TransactionStatusDto;
import com.nagarro.account_management_service.entities.Account;
import com.nagarro.account_management_service.exceptions.AccountNotFoundException;
import com.nagarro.account_management_service.exceptions.CustomerAlreadyHasSameTypeAccountException;
import com.nagarro.account_management_service.exceptions.CustomerDetailsNotFetchedException;
import com.nagarro.account_management_service.exceptions.CustomerNotFoundException;
import com.nagarro.account_management_service.exceptions.InsufficientBalanceException;
import com.nagarro.account_management_service.exceptions.UnAuthorizedTransactionException;
import com.nagarro.account_management_service.services.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/account")
public class AccountController {
	private AccountService accountService;
	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createAccount(@Valid @RequestBody Account account, BindingResult bindingResult){
		
		if(bindingResult.hasErrors()) {
			// get a field error to display to the client
			FieldError fieldError = bindingResult.getFieldError();
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage(fieldError.getDefaultMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		try {
			Account createdAccount = accountService.createAccount(account);
			// construct success response
			SuccessResponse<Account> successResponse = new SuccessResponse<>();
			successResponse.setData(createdAccount);
			return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
			
		} catch (CustomerNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage("Cannot create account! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse ,HttpStatus.BAD_REQUEST);
			
		} catch (CustomerDetailsNotFetchedException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(502);
			errorResponse.setMessage(exception.getMessage()+". Create account failed! Please try again.");
			return new ResponseEntity<>(errorResponse ,HttpStatus.BAD_GATEWAY);
			
		} catch (CustomerAlreadyHasSameTypeAccountException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(409);
			errorResponse.setMessage("Cannot create account! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse ,HttpStatus.CONFLICT);
			
		} catch (Exception exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/get/{accountId}")
	public ResponseEntity<?> getAccountDetails(@PathVariable("accountId") int accountId) {
		try{
			AccountDetailsDto accountDetailsDto = this.accountService.getAccountDetails(accountId);
			// construct success response
			SuccessResponse<AccountDetailsDto> successResponse = new SuccessResponse<>();
			successResponse.setData(accountDetailsDto);
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
			
		} catch (AccountNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(404);
			errorResponse.setMessage(exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
			
		} catch (CustomerDetailsNotFetchedException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(502);
			errorResponse.setMessage(exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
			
		} catch (Exception exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/deposit")
	public ResponseEntity<?> deposit(@Valid @RequestBody TransactionDto transactionDto, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			// get a field error to display to the client
			FieldError fieldError = bindingResult.getFieldError();
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage(fieldError.getDefaultMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		try{
			TransactionStatusDto transactionStatusDto = this.accountService.deposit(transactionDto);
			// construct success response
			SuccessResponse<TransactionStatusDto> successResponse = new SuccessResponse<>();
			successResponse.setData(transactionStatusDto);
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
			
		} catch (AccountNotFoundException | CustomerNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage("Cannot deposit! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			
		} catch (CustomerDetailsNotFetchedException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(502);
			errorResponse.setMessage("Customer details could not be verified. REASON:"+exception.getMessage()+". Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
			
		} catch (UnAuthorizedTransactionException exception) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(401);
			errorResponse.setMessage("Cannot deposit! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
			
		} catch (Exception exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/withdraw")
	public ResponseEntity<?> withdraw(@Valid @RequestBody TransactionDto transactionDto, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			// get a field error to display to the client
			FieldError fieldError = bindingResult.getFieldError();
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage(fieldError.getDefaultMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		try{
			TransactionStatusDto transactionStatusDto = this.accountService.withdraw(transactionDto);
			// construct success response
			SuccessResponse<TransactionStatusDto> successResponse = new SuccessResponse<>();
			successResponse.setData(transactionStatusDto);
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
			
		} catch (AccountNotFoundException | CustomerNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage("Cannot withdraw! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			
		} catch (CustomerDetailsNotFetchedException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(502);
			errorResponse.setMessage("Customer details could not be verified. REASON:"+exception.getMessage()+". Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
			
		} catch (InsufficientBalanceException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(402);
			errorResponse.setMessage("Cannot withdraw! REASON: "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.PAYMENT_REQUIRED);
			
		} catch (UnAuthorizedTransactionException exception) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(401);
			errorResponse.setMessage("Cannot withdraw! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
			
		} catch (Exception exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/delete/{accountId}")
	public ResponseEntity<?> deleteAccount(@PathVariable("accountId") int id) {
		try {
			this.accountService.deleteAccount(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
		} catch (AccountNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(404);
			errorResponse.setMessage("Cannot delete! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
			
		} catch (Exception exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/delete/customer/{customerId}")
	public ResponseEntity<?> deleteAccountByCustomerId(@PathVariable("customerId") int customerId) {
		try{
			this.accountService.deleteAccountByCustomerId(customerId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
		} catch (Exception exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
