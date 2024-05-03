package com.nagarro.customer_management_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.customer_management_service.entities.Customer;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
