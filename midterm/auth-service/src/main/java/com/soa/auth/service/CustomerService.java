package com.soa.auth.service;

import com.soa.auth.entity.Customer;
import com.soa.auth.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for customer operations.
 * Handles business logic related to customer management.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Find and lock customer by ID for update (pessimistic lock).
     * Used during financial transactions to prevent concurrent modifications.
     * 
     * @param id Customer ID
     * @return Optional containing the locked customer
     */
    public Optional<Customer> findByIdForUpdate(Long id) {
        return customerRepository.findByIdForUpdate(id);
    }

    /**
     * Find customer by username.
     * Used for authentication and profile operations.
     * 
     * @param username Customer username
     * @return Optional containing the customer
     */
    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    /**
     * Save or update customer.
     * 
     * @param customer Customer entity to save
     * @return Saved customer entity
     */
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
