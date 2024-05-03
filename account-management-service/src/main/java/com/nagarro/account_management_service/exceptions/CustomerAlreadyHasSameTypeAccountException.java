package com.nagarro.account_management_service.exceptions;

public class CustomerAlreadyHasSameTypeAccountException extends RuntimeException {
	public CustomerAlreadyHasSameTypeAccountException() {
		super("Customer has already an account of same type");
	}
}
