package com.nagarro.account_management_service.exceptions;

public class UnAuthorizedTransactionException extends RuntimeException {
	public UnAuthorizedTransactionException() {
		super("Unauthorized! Cannot perform transactions on others' account");
	}
}
