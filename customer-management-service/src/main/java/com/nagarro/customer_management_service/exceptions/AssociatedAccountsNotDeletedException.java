package com.nagarro.customer_management_service.exceptions;

public class AssociatedAccountsNotDeletedException extends RuntimeException {
	public AssociatedAccountsNotDeletedException() {
		super("Failed to delete associated accounts!");
	}
}
