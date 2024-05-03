package com.nagarro.customer_management_service.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerUpdateDto {
	@NotNull(message = "Customer id is missing")
	private int customerId;
	@NotBlank(message = "name cannot be blank")
	private String name;
	@Email(message = "email is invalid")
	@NotNull(message = "email must be provided")
	private String email;
	@NotBlank(message = "password cannot be blank")
	@Size(min = 4, message = "password must be at least 4 characters long")
	private String password;
	private LocalDate dob;
	private String nationality;
}
