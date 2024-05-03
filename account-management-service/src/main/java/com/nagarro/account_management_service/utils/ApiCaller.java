package com.nagarro.account_management_service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.nagarro.account_management_service.DTOs.GetCustomerSuccessDto;
@Component
public class ApiCaller {
	@Autowired
	CustomerManagementServiceFeignClient customerManagementServiceFeignClient;
	
	// sample
	public ResponseEntity<GetCustomerSuccessDto> getCustomerDetails(int customerId) {
		try {
			return this.customerManagementServiceFeignClient.getCustomer(customerId);
		} catch (Exception exception) {
			throw exception;
		}
	}
}
