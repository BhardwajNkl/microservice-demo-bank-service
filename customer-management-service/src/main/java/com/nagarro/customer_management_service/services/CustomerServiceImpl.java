package com.nagarro.customer_management_service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.nagarro.customer_management_service.dto.CustomerGetDto;
import com.nagarro.customer_management_service.dto.CustomerPostDto;
import com.nagarro.customer_management_service.dto.CustomerUpdateDto;
import com.nagarro.customer_management_service.entities.Customer;
import com.nagarro.customer_management_service.exceptions.AssociatedAccountsNotDeletedException;
import com.nagarro.customer_management_service.exceptions.CustomerNotFoundException;
import com.nagarro.customer_management_service.exceptions.EmailExistsException;
import com.nagarro.customer_management_service.repositories.CustomerRepository;
import com.nagarro.customer_management_service.utils.ApiCaller;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {
	private CustomerRepository customerRepository;
	private ApiCaller apiCaller;
	
	@Autowired
	public CustomerServiceImpl(CustomerRepository customerRepository,
			ApiCaller apiCaller) {
		this.customerRepository = customerRepository;
		this.apiCaller = apiCaller;
	}
	
	@Override
	public CustomerGetDto createCustomer(CustomerPostDto customerPostDto) {
		// convert DTO to entity
		Customer customer = new Customer();
		customer.setName(customerPostDto.getName());
		customer.setEmail(customerPostDto.getEmail());
		customer.setPassword(customerPostDto.getPassword());
		customer.setDob(customerPostDto.getDob());
		customer.setNationality(customerPostDto.getNationality());
		
		try{
			// save in database
			this.customerRepository.save(customer);
			// return
			CustomerGetDto customerGetDto = new CustomerGetDto();
			customerGetDto.setCustomerId(customer.getCustomerId());
			customerGetDto.setName(customer.getName());
			customerGetDto.setEmail(customer.getEmail());
			customerGetDto.setDob(customer.getDob());
			customerGetDto.setNationality(customer.getNationality());
			return customerGetDto;
			
		}catch (DataIntegrityViolationException exception) {
			exception.printStackTrace();
			throw new EmailExistsException();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	@Override
	public List<CustomerGetDto> getAllCustomers() {
		try {
			List<Customer> customers = this.customerRepository.findAll();
			List<CustomerGetDto> customerGetDtos;
			if(customers.size()>0) {
				// map each entity to DTO
				customerGetDtos = customers.stream()
				.map((Customer customer)->{
					CustomerGetDto customerGetDto = new CustomerGetDto();
					customerGetDto.setCustomerId(customer.getCustomerId());
					customerGetDto.setName(customer.getName());
					customerGetDto.setEmail(customer.getEmail());
					customerGetDto.setDob(customer.getDob());
					customerGetDto.setNationality(customer.getNationality());
					return customerGetDto;
				})
				.collect(Collectors.toList());
			} else {
				customerGetDtos = new ArrayList<>();
			}
						
			return customerGetDtos;
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	@Override
	public CustomerGetDto getCustomer(int customerId) {
		try {
			Customer customer = this.customerRepository.findById(customerId)
					.orElseThrow(()-> new CustomerNotFoundException());
			
			// convert to DTO
			CustomerGetDto customerGetDto = new CustomerGetDto();
			customerGetDto.setCustomerId(customer.getCustomerId());
			customerGetDto.setName(customer.getName());
			customerGetDto.setEmail(customer.getEmail());
			customerGetDto.setDob(customer.getDob());
			customerGetDto.setNationality(customer.getNationality());
			return customerGetDto;
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	@Override
	public CustomerGetDto updateCustomer(CustomerUpdateDto customerUpdateDto) {
		try {
			Customer customer = customerRepository.findById(customerUpdateDto.getCustomerId())
					.orElseThrow(()-> new CustomerNotFoundException());
			
			// update details
			customer.setName(customerUpdateDto.getName());
			customer.setEmail(customerUpdateDto.getEmail());
			customer.setPassword(customerUpdateDto.getPassword());
			customer.setDob(customerUpdateDto.getDob());
			customer.setNationality(customerUpdateDto.getNationality());
			this.customerRepository.save(customer);
			
			// return a corresponding DTO
			CustomerGetDto customerGetDto = new CustomerGetDto();
			customerGetDto.setCustomerId(customer.getCustomerId());
			customerGetDto.setName(customer.getName());
			customerGetDto.setEmail(customer.getEmail());
			customerGetDto.setDob(customer.getDob());
			customerGetDto.setNationality(customer.getNationality());
			return customerGetDto;
			
		} catch (DataIntegrityViolationException exception) {
			exception.printStackTrace();
			throw new EmailExistsException();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	
	@Override
	@Transactional
	public void deleteCustomer(int customerId) {
		try {
			// check if customer exists
			Customer customer = this.customerRepository.findById(customerId)
					.orElseThrow(()-> new CustomerNotFoundException());
			// delete customer
			this.customerRepository.delete(customer);
			// delete accounts associated with this customer
			this.apiCaller.deleteCustomerAccount(customerId);
			
		} catch (FeignException exception) {
			// accounts associated with this customer could not be deleted
			throw new AssociatedAccountsNotDeletedException();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

}
