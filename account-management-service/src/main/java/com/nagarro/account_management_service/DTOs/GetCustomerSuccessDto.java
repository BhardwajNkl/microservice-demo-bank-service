package com.nagarro.account_management_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetCustomerSuccessDto {
	private CustomerDetailsDto data;
}
