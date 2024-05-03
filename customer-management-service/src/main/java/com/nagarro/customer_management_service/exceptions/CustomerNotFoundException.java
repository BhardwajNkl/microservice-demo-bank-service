package com.nagarro.customer_management_service.exceptions;

public class CustomerNotFoundException extends RuntimeException {
	public CustomerNotFoundException() {
		super("Customer with given Id does not exist");
	}
}
