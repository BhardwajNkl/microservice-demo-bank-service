package com.nagarro.customer_management_service.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ACCOUNT-MANAGEMENT-SERVICE")
public interface AccountManagementServiceFeignClient {

    @DeleteMapping("/api/account/delete/customer/{customerId}")
    ResponseEntity<?> deleteCustomerAccount(@PathVariable("customerId") int customerId);
}