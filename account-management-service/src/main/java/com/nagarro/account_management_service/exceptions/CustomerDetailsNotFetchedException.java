package com.nagarro.account_management_service.exceptions;

public class CustomerDetailsNotFetchedException extends RuntimeException {
	public CustomerDetailsNotFetchedException() {
		super("Customer details could not be fetched");
	}
}
