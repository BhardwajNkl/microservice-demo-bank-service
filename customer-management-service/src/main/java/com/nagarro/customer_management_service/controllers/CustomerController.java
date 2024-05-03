package com.nagarro.customer_management_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.customer_management_service.dto.CustomerGetDto;
import com.nagarro.customer_management_service.dto.CustomerPostDto;
import com.nagarro.customer_management_service.dto.CustomerUpdateDto;
import com.nagarro.customer_management_service.dto.ErrorResponse;
import com.nagarro.customer_management_service.dto.SuccessResponse;
import com.nagarro.customer_management_service.exceptions.AssociatedAccountsNotDeletedException;
import com.nagarro.customer_management_service.exceptions.CustomerNotFoundException;
import com.nagarro.customer_management_service.exceptions.EmailExistsException;
import com.nagarro.customer_management_service.services.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
	
	private CustomerService customerService;
	
	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	@PostMapping("/create")
	ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerPostDto customerPostDto, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			// getting a field error to show to the client
	        FieldError fieldError = bindingResult.getFieldError();
	        
	        // constructing error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage(fieldError.getDefaultMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		try {
			// create customer
			CustomerGetDto customerGetDto = this.customerService.createCustomer(customerPostDto);
			
			// construct success response
			SuccessResponse<CustomerGetDto> successResponse = new SuccessResponse<>();
			successResponse.setData(customerGetDto);
			return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
			
		} catch (EmailExistsException exception) {
			// construct an error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(409);
			errorResponse.setMessage(exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
			
		} catch (Exception exception) {
			// construct an error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/get")
	ResponseEntity<?> getAllCustomers(){
		try{
			// fetch the list of customers
			List<CustomerGetDto> customerGetDtos = this.customerService.getAllCustomers();
			// construct success response 
			SuccessResponse<List<CustomerGetDto>> successResponse = new SuccessResponse<>();
			successResponse.setData(customerGetDtos);
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
			
		} catch (Exception exception) {
			// construct an error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/get/{customerId}")
	ResponseEntity<?> getCustomer(@PathVariable("customerId") int customerId) {
		try {
			CustomerGetDto customerGetDto = this.customerService.getCustomer(customerId);
			// construct success response
			SuccessResponse<CustomerGetDto> successResponse = new SuccessResponse<>();
			successResponse.setData(customerGetDto);
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
			
		} catch (CustomerNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(404);
			errorResponse.setMessage(exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
			
		} catch (Exception exception) {
			// construct an error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/update")
	ResponseEntity<?> updateCustomer(@Valid @RequestBody CustomerUpdateDto customerUpdateDto, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			// getting a field error to display to the client
	        FieldError fieldError = bindingResult.getFieldError();
	        // construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(400);
			errorResponse.setMessage(fieldError.getDefaultMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		try {
			CustomerGetDto customerGetDto = this.customerService.updateCustomer(customerUpdateDto);
			// construct success response
			SuccessResponse<CustomerGetDto> successResponse = new SuccessResponse<>();
			successResponse.setData(customerGetDto);
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
			
		} catch (CustomerNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(404);
			errorResponse.setMessage("Cannot update! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
			
		} catch (EmailExistsException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(409);
			errorResponse.setMessage("Cannot update! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
			
		} catch (Exception exception) {
			// construct an error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/delete/{customerId}")
	ResponseEntity<?> deleteCustomer(@PathVariable("customerId") int customerId) {
		try {
			this.customerService.deleteCustomer(customerId);
			// construct success response
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
		} catch (CustomerNotFoundException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(404);
			errorResponse.setMessage("Cannot delete! "+exception.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
			
		} catch (AssociatedAccountsNotDeletedException exception) {
			// construct error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(502);
			errorResponse.setMessage(exception.getMessage()+" Customer delete failed!");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
			
		} catch (Exception exception) {
			// construct an error response
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(500);
			errorResponse.setMessage("Something went wrong! Please try again.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
