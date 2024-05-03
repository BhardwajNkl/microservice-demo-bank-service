package com.nagarro.account_management_service.exceptions;

public class AccountNotFoundException extends RuntimeException {
	public AccountNotFoundException() {
		super("Account with given Id does not exist");
	}
}
