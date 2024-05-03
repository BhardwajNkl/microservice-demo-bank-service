package com.nagarro.customer_management_service.exceptions;

public class EmailExistsException extends RuntimeException {
	public EmailExistsException() {
		super("Email already exists!");
	}
}
