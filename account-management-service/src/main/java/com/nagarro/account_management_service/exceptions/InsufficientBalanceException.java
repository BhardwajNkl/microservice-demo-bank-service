package com.nagarro.account_management_service.exceptions;

public class InsufficientBalanceException extends RuntimeException {
	public InsufficientBalanceException() {
		super("Insufficient balance");
	}
}
