package com.nagarro.account_management_service.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionDto {
	@NotNull(message = "customerId cannot be null")
	private int customerId;
	@NotNull(message = "accountId cannot be null")
	private int accountId;
	@NotNull(message = "amount cannot be null")
	@Min(value = 1, message = "amount must be greater than 0")
	private double amount;
}
