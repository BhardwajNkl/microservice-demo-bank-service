package com.nagarro.customer_management_service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
@Component
public class ApiCaller {
	
	@Autowired
	AccountManagementServiceFeignClient accountManagementServiceFeignClient;
	
	public ResponseEntity<?> deleteCustomerAccount(int customerId) {
		return accountManagementServiceFeignClient.deleteCustomerAccount(customerId);
	}	
}
