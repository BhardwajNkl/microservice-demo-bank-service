package com.nagarro.account_management_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.account_management_service.entities.Account;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface AccountRepository extends JpaRepository<Account, Integer>{
	void deleteAllByCustomerId(int customerId);
}
