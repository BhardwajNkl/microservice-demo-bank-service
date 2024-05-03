package com.nagarro.customer_management_service.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerGetDto {
	private int customerId;
	private String name;
	private String email;
	private LocalDate dob;
	private String nationality;
}
