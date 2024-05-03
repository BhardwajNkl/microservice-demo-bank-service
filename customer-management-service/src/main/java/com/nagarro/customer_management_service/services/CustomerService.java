package com.nagarro.customer_management_service.services;

import java.util.List;

import com.nagarro.customer_management_service.dto.CustomerGetDto;
import com.nagarro.customer_management_service.dto.CustomerPostDto;
import com.nagarro.customer_management_service.dto.CustomerUpdateDto;

public interface CustomerService {
	CustomerGetDto createCustomer(CustomerPostDto customerPostDto);
	List<CustomerGetDto> getAllCustomers();
	CustomerGetDto getCustomer(int customerId);
	CustomerGetDto updateCustomer(CustomerUpdateDto customerUpdateDto);
	void deleteCustomer(int customerId);
}
