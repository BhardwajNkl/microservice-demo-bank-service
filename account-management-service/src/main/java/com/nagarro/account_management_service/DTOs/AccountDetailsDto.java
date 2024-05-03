package com.nagarro.account_management_service.DTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountDetailsDto {
	private int accountId;
	private String type;
	private int customerId;
	private String name;
	private String email;
	private LocalDate dob;
	private String nationality;
	private double balance;
	private String status;
}
