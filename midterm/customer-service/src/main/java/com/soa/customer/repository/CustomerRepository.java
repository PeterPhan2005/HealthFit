package com.soa.customer.repository;

import com.soa.customer.entity.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for Customer entity.
 * Provides database operations for customer management.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by ID with pessimistic write lock for transaction safety.
     * This prevents concurrent modifications during financial transactions.
     * 
     * @param id Customer ID
     * @return Optional containing the locked customer or empty if not found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Optional<Customer> findByIdForUpdate(@Param("id") Long id);

    /**
     * Find customer by username for authentication.
     * 
     * @param username Customer username
     * @return Customer entity or null if not found
     */
    Optional<Customer> findByUsername(String username);
}
