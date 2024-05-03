package com.nagarro.account_management_service.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nagarro.account_management_service.DTOs.GetCustomerSuccessDto;

@FeignClient(name = "CUSTOMER-MANAGEMENT-SERVICE")
public interface CustomerManagementServiceFeignClient {
	@GetMapping("api/customer/get/{customerId}")
    ResponseEntity<GetCustomerSuccessDto> getCustomer(@PathVariable int customerId);
}
